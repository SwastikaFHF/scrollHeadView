package com.aitangba.testproject.view.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by fhf11991 on 2017/6/13.
 */

public class AverageLayout extends LinearLayout {

    public AverageLayout(Context context) {
        super(context);
    }

    public AverageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AverageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int parentWidth = getMeasuredWidth();
        int childNeedWidth = 0;
        int childCount = 0;
        for(int i = 0; i < getChildCount() ; i++) {
            View child = getChildAt(i);
            if(child.getVisibility() == GONE) {
                continue;
            }
            childNeedWidth = childNeedWidth + getChildAt(i).getMeasuredWidth();
            childCount = childCount + 1;
        }

        final int marginLeft = (parentWidth - childNeedWidth) / (childCount + 1);

        int tempLeft = marginLeft;
        for(int i = 0; i < getChildCount() ; i++) {
            View child = getChildAt(i);
            if(child.getVisibility() == GONE) {
                continue;
            }
            int width = child.getMeasuredWidth();
            child.layout(tempLeft, 0, tempLeft + width, child.getMeasuredHeight());
            tempLeft = tempLeft + width + marginLeft;
        }
    }
}
