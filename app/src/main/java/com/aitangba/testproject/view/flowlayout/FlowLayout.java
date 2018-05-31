package com.aitangba.testproject.view.flowlayout;

import android.content.Context;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by fhf11991 on 2017/5/18.
 */

public class FlowLayout extends ViewGroup {
    private final static String TAG = "FlowLayout";
    private @Gravity
    int mGravity = CENTER;
    private int mLeftMargin = 10; // the same as leftMargin,every first child do not use leftMargin
    private int mTopMargin = 10;  // the same as topMargin,every first row do not use topMargin

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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int childCount = getChildCount();
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int availableWidth = width - getPaddingLeft() - getPaddingRight();

        int row = 0; // in case,first child is out of bounds
        int column = 0;
        int leftMarginTemp = 0;
        int maxHeightFromSameRow = 0;
        int maxWidth = 0;
        int maxHeight = getPaddingTop() + getPaddingBottom();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            final int childHeight = child.getMeasuredHeight();
            final int childWidth = child.getMeasuredWidth();
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();

            final boolean firstColumn = column == 0;
            final boolean lastItemView = i == Math.max(0, childCount - 1);
            final int neededWidth = leftMarginTemp + (firstColumn ? 0 : mLeftMargin) + childWidth;
            final boolean enoughWidth = neededWidth <= availableWidth;
            final int currentItemHeight = childHeight + (row == 0 ? 0 : mTopMargin);

            if(lastItemView && enoughWidth) {
                notifyCurrentRowViews(i, row, Math.max(maxHeightFromSameRow, currentItemHeight));
                layoutParams.maxHeightFromSameRow = Math.max(maxHeightFromSameRow, currentItemHeight);
            } else if(lastItemView) {
                notifyCurrentRowViews(i, row, maxHeightFromSameRow);
                layoutParams.maxHeightFromSameRow = currentItemHeight;
            } else if(!enoughWidth) {
                notifyCurrentRowViews(i, row, maxHeightFromSameRow);
                layoutParams.maxHeightFromSameRow = currentItemHeight;
            }

            if (firstColumn && enoughWidth) {
                layoutParams.setPosition(row, column);
                column = column + 1;
            } else if (firstColumn) {
                layoutParams.setPosition(row, column);
                row = row + 1;
                column = 0;
            } else if (enoughWidth) {
                layoutParams.setPosition(row, column);
                column = column + 1;
            } else {
                row = row + 1;
                column = 0;
                layoutParams.setPosition(row, column);
            }

            if(enoughWidth) {
                leftMarginTemp = leftMarginTemp + (firstColumn ? 0 : mLeftMargin) + childWidth;
                maxHeightFromSameRow = Math.max(maxHeightFromSameRow, currentItemHeight);
                maxWidth = Math.max(maxWidth, leftMarginTemp);
                maxHeight = maxHeight + (lastItemView ? maxHeightFromSameRow : 0);
            } else {
                int currentMaxWidth = leftMarginTemp;
                int lastMaxHeight = maxHeightFromSameRow;
                leftMarginTemp = childWidth;
                maxHeightFromSameRow = 0;
                maxWidth = firstColumn ? neededWidth : Math.max(maxWidth, currentMaxWidth);
                maxHeight = maxHeight + (firstColumn ? currentItemHeight : 0) + lastMaxHeight;
            }
            Log.d(TAG, " i = " + i
                    + " neededWidth = " + neededWidth
                    + " currentItemHeight = " + currentItemHeight
                    + " leftMarginTemp = " + leftMarginTemp
                    + " maxHeightFromSameRow = " + maxHeightFromSameRow
                    + " maxWidth = " + maxWidth
                    + " maxHeight = " + maxHeight );
        }

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthSizeAndState;
        if (widthMode == MeasureSpec.EXACTLY) {
            widthSizeAndState = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            widthSizeAndState = Math.min(maxWidth + getPaddingLeft() + getPaddingRight(), widthSize);
        } else {
            widthSizeAndState = widthSize;
        }

        int heightSizeAndState;
        if (heightMode == MeasureSpec.EXACTLY) {
            heightSizeAndState = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            heightSizeAndState = Math.min(maxHeight + getPaddingTop() + getPaddingBottom(), heightSize);
        } else {
            heightSizeAndState = heightSize;
        }
        setMeasuredDimension(widthSizeAndState, heightSizeAndState);
    }

    private void notifyCurrentRowViews(int currentIndex, int currentRow, int maxHeight) {
        for (int j = currentIndex - 1; j >= 0; j--) {
            LayoutParams previousLayoutParams = (LayoutParams) getChildAt(j).getLayoutParams();
            if (previousLayoutParams.row == currentRow) {
                previousLayoutParams.maxHeightFromSameRow = maxHeight;
            } else {
                break;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        int rowTemp = 0;
        int widthTemp = getPaddingLeft();
        int heightTemp = getPaddingTop();
        int previousRowHeight = 0; // just record, and adjust the height of every row !!!
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            int childHeight = child.getMeasuredHeight();
            int childWidth = child.getMeasuredWidth();
            int maxHeightFromSameRow = layoutParams.maxHeightFromSameRow;
            if (layoutParams.row != rowTemp) { // not the same row, reset some params
                widthTemp = getPaddingLeft();
                heightTemp = heightTemp + previousRowHeight + mTopMargin; // in this situation, every row must add extra vertical space !!!
            }
            int left = widthTemp;
            int top = heightTemp + getChildTopDiff(maxHeightFromSameRow, childHeight);
            child.layout(left, top, left + childWidth, top + childHeight);
            widthTemp = widthTemp + mLeftMargin + childWidth;
            previousRowHeight = maxHeightFromSameRow;
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

    private static class LayoutParams extends ViewGroup.LayoutParams {
        int row;
        int index;
        int maxHeightFromSameRow;

        LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        void setPosition(int row, int index) {
            this.row = row;
            this.index = index;
        }
    }

    @IntDef({TOP, CENTER, BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    @interface Gravity {
    }

    public final static int TOP = 1;
    public final static int CENTER = 2;
    public final static int BOTTOM = 3;
}
