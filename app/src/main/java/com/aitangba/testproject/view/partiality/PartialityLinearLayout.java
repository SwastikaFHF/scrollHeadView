package com.aitangba.testproject.view.partiality;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2018/5/25.
 */
public class PartialityLinearLayout extends ViewGroup {

    private static final String TAG = "Partiality";

    private int mGravity = CENTER;

    private static final int LEFT = 1;
    private static final int CENTER = 2;
    private static final int RIGHT = 3;

    public PartialityLinearLayout(Context context) {
        this(context, null);
    }

    public PartialityLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PartialityLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PartialityLinearLayout);
        this.mGravity = array.getInt(R.styleable.PartialityLinearLayout_gravity, CENTER);
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int surplusWidth = widthSize - measureSpace;
        int widthSpec = MeasureSpec.makeMeasureSpec(surplusWidth, MeasureSpec.getMode(widthMeasureSpec));

        int itemMaxHeight = 0;
        int itemWidth = 0;
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

            itemMaxHeight = Math.max(itemMaxHeight, child.getMeasuredHeight());
            itemWidth = itemWidth + child.getMeasuredWidth();
        }

        int widthSizeAndState;
        if(widthMode == MeasureSpec.EXACTLY) {
            widthSizeAndState = widthSize;
        } else if(widthMode == MeasureSpec.AT_MOST) {
            widthSizeAndState = Math.min(itemWidth + getPaddingLeft() + getPaddingRight(), widthSize);
        } else {
            widthSizeAndState = widthSize;
        }

        int heightSizeAndState;
        if(heightMode == MeasureSpec.EXACTLY) {
            heightSizeAndState = heightSize;
        } else if(heightMode == MeasureSpec.AT_MOST) {
            heightSizeAndState = Math.min(itemMaxHeight + getPaddingTop() + getPaddingBottom(), heightSize);
        } else {
            heightSizeAndState = heightSize;
        }

        setMeasuredDimension(widthSizeAndState, heightSizeAndState);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childTotalWidth = 0;
        for(int i = 0,count = getChildCount(); i < count ; i++) {
            View child = getChildAt(i);
            if(child.getVisibility() == GONE) {
                continue;
            }
            childTotalWidth = childTotalWidth + child.getMeasuredWidth();
        }

        int middle = getMeasuredHeight() / 2;
        int width = getMeasuredWidth();
        int childLeft;
        if(mGravity == LEFT) {
            childLeft = getPaddingLeft();
        } else if(mGravity == CENTER) {
            childLeft = (width - childTotalWidth) / 2;
        } else {
            childLeft = width - getPaddingRight() - childTotalWidth;
        }
        for(int i = 0,count = getChildCount(); i < count ; i++) {
            View child = getChildAt(i);
            if(child.getVisibility() == GONE) {
                continue;
            }

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            if(child.getLayoutParams() instanceof LayoutParams) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                childLeft += lp.leftMargin;
                setChildFrame(child, childLeft, middle - childHeight / 2, childWidth, childHeight);
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

    private static class LayoutParams extends MarginLayoutParams {

        private boolean primary;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PartialityLinearLayout_Layout);
            this.primary = array.getBoolean(R.styleable.PartialityLinearLayout_Layout_primary, false);
            array.recycle();
        }
    }
}
