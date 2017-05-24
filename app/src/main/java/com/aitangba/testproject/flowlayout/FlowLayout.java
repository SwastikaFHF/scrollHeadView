package com.aitangba.testproject.flowlayout;

import android.content.Context;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by fhf11991 on 2017/5/18.
 */

public class FlowLayout extends ViewGroup {

    private SparseArray<RomInfo> romInfoList = new SparseArray<>();

    private @Gravity int mGravity = BOTTOM;

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
        romInfoList.clear();

        int childCount = getChildCount();
        if(childCount == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        final int leftMargin = mHorizontalSpace;
        final int topMargin = mVerticalSpace;
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpace = widthSize - getPaddingLeft() - getPaddingRight();
        int widthSpaceNeed = 0;
        int rowMaxHeight = 0;

        int row = 0;
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

            if(widthSpaceNeed <= widthSpace) {
                rowMaxHeight = Math.max(rowMaxHeight, childHeight);

                layoutParams.setPosition(row, index);
                index = index + 1;
            } else {
                row = row + 1;
                index = 0;
                widthSpaceNeed = 0 + leftMargin + childWidth;
                rowMaxHeight = childHeight;

                layoutParams.setPosition(row, index);
                index = index + 1;
            }

            // find max height
            RomInfo romInfo = romInfoList.get(row);
            if(romInfo == null) {
                romInfoList.append(row, new RomInfo(widthSpaceNeed, childHeight));
            } else {
                romInfo.maxWidth = widthSpaceNeed;
                romInfo.maxHeight = Math.max(romInfo.maxHeight, childHeight);
            }
        }

        int maxHeight = 0;
        int maxWidth = 0;
        for(int i = 0; i < romInfoList.size(); i ++) {
            RomInfo romInfo = romInfoList.valueAt(i);

            if(i == 0) { // first row do not need topMargin,so do not add topMargin
                maxHeight = maxHeight + romInfo.maxHeight;
            } else {
                maxHeight = maxHeight + romInfo.maxHeight + topMargin;
            }

            // first child in every row do not need leftMargin,so remove it
            romInfo.maxWidth = romInfo.maxWidth - leftMargin;
            maxWidth = Math.max(maxWidth, romInfo.maxWidth);
        }

        int width;
        if(widthMode == MeasureSpec.AT_MOST) {
            width = maxWidth;
        } else {
            width = widthSize;
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

        int rowMaxHeight = 0;
        for(int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == View.GONE) {
                continue;
            }

            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            int childHeight = child.getMeasuredHeight();
            int childWidth = child.getMeasuredWidth();

            RomInfo romInfo = romInfoList.get(layoutParams.row);
            int maxHeight = 0;
            if(romInfo != null) {
                maxHeight = romInfo.maxHeight;
            }

            if(layoutParams.row == rowTemp) {
                int left = widthTemp;
                int top = heightTemp + getChildTopDiff(maxHeight, childHeight) + (layoutParams.row == 0 ? 0 : mVerticalSpace);
                child.layout(left, top, left + childWidth, top + childHeight);

                widthTemp = widthTemp + childWidth + mHorizontalSpace;
                rowMaxHeight = Math.max(rowMaxHeight, childHeight);
            } else {
                widthTemp = getPaddingLeft();
                heightTemp = heightTemp + rowMaxHeight;
                rowMaxHeight = 0;

                int left = widthTemp;
                int top = heightTemp + getChildTopDiff(maxHeight, childHeight) + (layoutParams.row == 0 ? 0 : mVerticalSpace);
                child.layout(left, top, left + childWidth, top + childHeight);

                widthTemp = widthTemp + childWidth + mHorizontalSpace;
                rowMaxHeight = Math.max(rowMaxHeight, childHeight);
            }

            rowTemp = layoutParams.row;
        }
    }

    private int getChildTopDiff(int maxHeight, int childHeight) {
        int diff;
        switch (mGravity) {
            case TOP:
                diff = 0;
                break;
            case CENTER:
                diff = (maxHeight - childHeight) / 2;
                break;
            case BOTTOM:
                diff = maxHeight - childHeight;
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

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {

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

    private static class RomInfo {
        public int maxWidth;
        public int maxHeight;

        public RomInfo(int maxWidth, int maxHeight) {
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
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
