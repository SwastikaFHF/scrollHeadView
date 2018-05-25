package com.aitangba.testproject.view.partiality;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2018/5/25.
 */
public class PartialityLinearLayout extends ViewGroup {

    private static final String TAG = "Partiality";
    public PartialityLinearLayout(Context context) {
        super(context);
    }

    public PartialityLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PartialityLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure --- width = " + MeasureSpec.toString(widthMeasureSpec) + "   height = " + MeasureSpec.toString(heightMeasureSpec));

        // measure primary children
        int measureSpace = 0;
        for(int i = 0,count = getChildCount(); i < count ; i++) {
            View child = getChildAt(i);
            if(child.getVisibility() == GONE) {
                continue;
            }
            if(child.getLayoutParams() instanceof LayoutParams) {
                LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                if(layoutParams.primary) {
                    measureChild(child, widthMeasureSpec, heightMeasureSpec);
                    measureSpace = measureSpace + child.getMeasuredWidth();
                }
            }
        }

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final boolean isExactly = widthMode == MeasureSpec.EXACTLY;

        int surplusWidth = widthSize - measureSpace;
        int widthSpec = MeasureSpec.makeMeasureSpec(surplusWidth, MeasureSpec.getMode(widthMeasureSpec));
        // measure other children
        for(int i = 0,count = getChildCount(); i < count ; i++) {
            View child = getChildAt(i);
            if(child.getVisibility() == GONE) {
                continue;
            }
            if(child.getLayoutParams() instanceof LayoutParams) {
                LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                if(!layoutParams.primary) {
                    measureChild(child, widthSpec, heightMeasureSpec);
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;
        int paddingTop = getPaddingTop();

        for(int i = 0,count = getChildCount(); i < count ; i++) {
            View child = getChildAt(i);
            if(child.getVisibility() == GONE) {
                continue;
            }

            int childWidth = child.getMeasuredWidth();
            if(child.getLayoutParams() instanceof LayoutParams) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                childLeft += lp.leftMargin;
                setChildFrame(child, childLeft, paddingTop + lp.topMargin, childWidth, child.getMeasuredHeight());
                childLeft += childWidth + lp.rightMargin;
            }
        }
    }

    private void setChildFrame(View child, int left, int top, int width, int height) {
        child.layout(left, top, left + width, top + height);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        Log.d(TAG, "AttributeSet---");
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        Log.d(TAG, "ViewGroup.LayoutParams---");
        return super.generateLayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    private static class LayoutParams extends ViewGroup.MarginLayoutParams {

        private boolean primary;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PartialityLinearLayout_Layout);
            this.primary = array.getBoolean(R.styleable.PartialityLinearLayout_Layout_primary, false);
            array.recycle();
        }
    }
}
