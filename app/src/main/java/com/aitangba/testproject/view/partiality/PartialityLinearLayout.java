package com.aitangba.testproject.view.partiality;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.aitangba.testproject.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by fhf11991 on 2018/5/25.
 */
public class PartialityLinearLayout extends ViewGroup {

    private static final String TAG = "Partiality";

    @IntDef({LEFT, CENTER, RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GravityMode {
    }

    private static final int LEFT = 1;
    private static final int TOP = 2;
    private static final int RIGHT = 3;
    private static final int BOTTOM = 4;
    private static final int CENTER = 5;

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {
    }

    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;

    private int mGravity = CENTER;
    private int mOrientation = HORIZONTAL;

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
        this.mOrientation = array.getInt(R.styleable.PartialityLinearLayout_orientation, HORIZONTAL);
        array.recycle();
    }

    public void setGravity(@GravityMode int gravity) {
        mGravity = gravity;
        requestLayout();
    }

    public void setOrientation(@OrientationMode int orientation) {
        mOrientation = orientation;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure --- width = " + MeasureSpec.toString(widthMeasureSpec) + "   height = " + MeasureSpec.toString(heightMeasureSpec));
        if (mOrientation == HORIZONTAL) {
            measureHorizontal(widthMeasureSpec, heightMeasureSpec);
        } else {
            measureVertical(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void measureHorizontal(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // measure primary children
        int primaryTotalWidth = 0;
        int primaryMaxHeight = 0;
        for (int i = 0, count = getChildCount(); i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            if (child.getLayoutParams() instanceof LayoutParams) {
                LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                if (layoutParams.primary) {
                    measureChild(child, widthMeasureSpec, heightMeasureSpec);
                    primaryTotalWidth = primaryTotalWidth + layoutParams.leftMargin + child.getMeasuredWidth() + layoutParams.rightMargin;
                    primaryMaxHeight = Math.max(primaryMaxHeight, layoutParams.topMargin + child.getMeasuredHeight() + layoutParams.bottomMargin);
                }
            }
        }

        final int widthSpec = MeasureSpec.makeMeasureSpec(widthSize - primaryTotalWidth, MeasureSpec.getMode(widthMeasureSpec));

        int secondaryTotalWidth = 0;
        int secondaryMaxHeight = 0;
        // measure other children
        for (int i = 0, count = getChildCount(); i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            if (child.getLayoutParams() instanceof LayoutParams) {
                LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                if (!layoutParams.primary) {
                    measureChild(child, widthSpec, heightMeasureSpec);
                    secondaryMaxHeight = Math.max(secondaryMaxHeight, child.getMeasuredHeight());
                    secondaryTotalWidth = secondaryTotalWidth + layoutParams.leftMargin + child.getMeasuredWidth() + layoutParams.rightMargin;
                }
            }
        }

        int widthSizeAndState;
        if (widthMode == MeasureSpec.EXACTLY) {
            widthSizeAndState = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            widthSizeAndState = Math.min(getPaddingLeft() + secondaryTotalWidth + primaryTotalWidth + getPaddingRight(), widthSize);
        } else {
            widthSizeAndState = widthSize;
        }

        int heightSizeAndState;
        if (heightMode == MeasureSpec.EXACTLY) {
            heightSizeAndState = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            int childMaxHeight = Math.max(primaryMaxHeight, secondaryMaxHeight);
            heightSizeAndState = Math.min(childMaxHeight + getPaddingTop() + getPaddingBottom(), heightSize);
        } else {
            heightSizeAndState = heightSize;
        }

        setMeasuredDimension(widthSizeAndState, heightSizeAndState);
    }

    private void measureVertical(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // measure primary children
        int primaryTotalHeight = 0;
        int primaryMaxWidth = 0;
        for (int i = 0, count = getChildCount(); i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            if (child.getLayoutParams() instanceof LayoutParams) {
                LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                if (layoutParams.primary) {
                    measureChild(child, widthMeasureSpec, heightMeasureSpec);
                    primaryTotalHeight = primaryTotalHeight + layoutParams.topMargin + child.getMeasuredHeight() + layoutParams.bottomMargin;
                    primaryMaxWidth = Math.max(primaryMaxWidth, layoutParams.leftMargin + child.getMeasuredWidth() + layoutParams.rightMargin);
                }
            }
        }

        final int heightSpec = MeasureSpec.makeMeasureSpec(heightSize - primaryTotalHeight, heightMode);

        int secondaryTotalHeight = 0;
        int secondaryMaxWidth = 0;
        // measure other children
        for (int i = 0, count = getChildCount(); i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            if (child.getLayoutParams() instanceof LayoutParams) {
                LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                if (!layoutParams.primary) {
                    measureChild(child, widthMeasureSpec, heightSpec);
                    secondaryTotalHeight = secondaryTotalHeight + layoutParams.topMargin + child.getMeasuredHeight() + layoutParams.bottomMargin;
                    secondaryMaxWidth = Math.max(secondaryMaxWidth, layoutParams.leftMargin + child.getMeasuredWidth() + layoutParams.rightMargin);
                }
            }
        }

        int widthSizeAndState;
        if (widthMode == MeasureSpec.EXACTLY) {
            widthSizeAndState = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            int childMaxWidth = Math.max(primaryMaxWidth, secondaryMaxWidth);
            widthSizeAndState = Math.min(childMaxWidth + getPaddingLeft() + getPaddingRight(), widthSize);
        } else {
            widthSizeAndState = widthSize;
        }

        int heightSizeAndState;
        if (heightMode == MeasureSpec.EXACTLY) {
            heightSizeAndState = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            heightSizeAndState = Math.min(getPaddingTop() + primaryTotalHeight + secondaryTotalHeight + getPaddingBottom(), heightSize);
        } else {
            heightSizeAndState = heightSize;
        }
        setMeasuredDimension(widthSizeAndState, heightSizeAndState);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mOrientation == HORIZONTAL) {
            layoutHorizontal(l, t, r, b);
        } else {
            layoutVertical(l, t, r, b);
        }
    }

    private void layoutHorizontal(int l, int t, int r, int b) {
        int childTop;
        int tempWidth = l;
        for (int i = 0, count = getChildCount(); i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            if (child.getLayoutParams() instanceof LayoutParams) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (mGravity == TOP) {
                    childTop = t + lp.topMargin;
                } else if (mGravity == BOTTOM) {
                    childTop = b - childHeight - lp.bottomMargin;
                } else if (mGravity == CENTER) {
                    childTop = (b - childHeight) / 2;
                } else {
                    childTop = (b - childHeight) / 2;
                }
                tempWidth = tempWidth + lp.leftMargin;
                setChildFrame(child, tempWidth, childTop, childWidth, childHeight);
                tempWidth = tempWidth + childWidth + lp.rightMargin;
            }
        }
    }

    private void layoutVertical(int l, int t, int r, int b) {
        int childLeft;
        int tempHeight = t;
        for (int i = 0, count = getChildCount(); i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            if (child.getLayoutParams() instanceof LayoutParams) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (mGravity == LEFT) {
                    childLeft = l + lp.leftMargin;
                } else if (mGravity == RIGHT) {
                    childLeft = r - childWidth - lp.rightMargin;
                } else if (mGravity == CENTER) {
                    childLeft = (r - childWidth) / 2 + lp.leftMargin;
                } else {
                    childLeft = (r - childWidth) / 2 + lp.leftMargin;
                }
                tempHeight = tempHeight + lp.topMargin;
                setChildFrame(child, childLeft, tempHeight, childWidth, childHeight);
                tempHeight = tempHeight + childHeight;
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
