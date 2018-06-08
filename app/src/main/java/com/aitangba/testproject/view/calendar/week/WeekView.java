package com.aitangba.testproject.view.calendar.week;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by fhf11991 on 2018/6/8
 */
public class WeekView extends ViewGroup {

    private static final int COLUMN_SIZE = 7;

    public WeekView(Context context) {
        super(context);
    }

    public WeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthLimit = widthSize / COLUMN_SIZE;
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthLimit, MeasureSpec.EXACTLY);

        int itemHeight = -1;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            if (itemHeight == -1) { // 获取第一个可见的cell的高度
                child.measure(childWidthMeasureSpec, heightMeasureSpec);
                itemHeight = child.getMeasuredHeight();
            } else {
                int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY);
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
        setMeasuredDimension(widthSize, getPaddingTop() + Math.max(0, itemHeight) + getPaddingBottom());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();

        int widthTemp = getPaddingLeft();
        int heightTemp = getPaddingTop();
        int index = 0;
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
