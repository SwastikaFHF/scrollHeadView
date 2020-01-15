package com.aitangba.testproject.html;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by XBeats on 2020/1/10
 */
public class BaseDialog extends Dialog {

    private final TypedValue mWidthValue = new TypedValue();
    private boolean mWindowFullscreen;

    public BaseDialog(@NonNull Context context) {
        this(context, 0);
        init();
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    private void init() {
        final DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        getContext().getTheme().resolveAttribute(android.R.attr.windowMinWidthMinor, mWidthValue, true);

        if (mWidthValue.type != TypedValue.TYPE_NULL) {
            final int w;
            if (mWidthValue.type == TypedValue.TYPE_DIMENSION) {
                w = (int) mWidthValue.getDimension(metrics);
            } else if (mWidthValue.type == TypedValue.TYPE_FRACTION) {
                w = (int) mWidthValue.getFraction(metrics.widthPixels, metrics.widthPixels);
            } else {
                w = 0;
            }
            mWindowFullscreen = w >= metrics.widthPixels;
        }
    }

    @Override
    public void setContentView(@NonNull View view) {
        if (mWindowFullscreen) {
            super.setContentView(view);
        } else {
            super.setContentView(wrapInBottomSheet(0, view, null), getLayoutParams());
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        if (mWindowFullscreen) {
            super.setContentView(layoutResID);
        } else {
            super.setContentView(wrapInBottomSheet(layoutResID, null, null), getLayoutParams());
        }
    }

    @Override
    public void setContentView(@NonNull View view, @Nullable ViewGroup.LayoutParams params) {
        if (mWindowFullscreen) {
            super.setContentView(view, params);
        } else {
            super.setContentView(wrapInBottomSheet(0, view, params), getLayoutParams());
        }
    }

    private View wrapInBottomSheet(int layoutResId, View view, ViewGroup.LayoutParams params) {
        MaxHeightFrameLayout innerView = new MaxHeightFrameLayout(getContext());

        if (layoutResId != 0) {
            view = getLayoutInflater().inflate(layoutResId, innerView, false);
        }

        if (params != null) {
            innerView.addView(view, params);
        } else {
            innerView.addView(view);
        }
        return innerView;
    }

    private FrameLayout.LayoutParams getLayoutParams() {
        return new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }

    private class MaxHeightFrameLayout extends FrameLayout {

        private static final float sMaxHeight = 0.9F;

        private MaxHeightFrameLayout(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (mWindowFullscreen) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            } else {
                int height = MeasureSpec.getSize(heightMeasureSpec);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (height * sMaxHeight), MeasureSpec.EXACTLY);
                measureChildren(widthMeasureSpec, heightMeasureSpec);
                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
            }
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            for (int i = 0, count = getChildCount(); i < count; i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() != GONE) {
                    int childTop;
                    int childBottom;
                    if (mWindowFullscreen) {
                        childTop = bottom - child.getMeasuredHeight();
                        childBottom = bottom;
                    } else {
                        int dis = (int) (getMeasuredHeight() * ((1 - sMaxHeight) / 2));
                        childTop = top + dis;
                        childBottom = bottom - dis;
                    }
                    child.layout(left, childTop, right, childBottom);
                }
            }
        }
    }
}
