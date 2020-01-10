package com.aitangba.testproject.html;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.aitangba.testproject.R;

/**
 * Created by XBeats on 2020/1/10
 */
public class BaseDialog extends Dialog {

    private final TypedValue mWidthValue = new TypedValue();
    private final TypedValue mHeightValue = new TypedValue();
    private boolean mAdjustWindowHeight = false;

    public BaseDialog(@NonNull Context context) {
        this(context, 0);
        getContext().getTheme().resolveAttribute(R.attr.windowWidth, mWidthValue, true);
        getContext().getTheme().resolveAttribute(R.attr.windowHeight, mHeightValue, true);
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        getContext().getTheme().resolveAttribute(R.attr.windowWidth, mWidthValue, true);
        getContext().getTheme().resolveAttribute(R.attr.windowHeight, mHeightValue, true);
    }

    @Override
    public void setContentView(@NonNull View view) {
        super.setContentView(wrapInBottomSheet(0, view, null));
    }

    @Override
    public void setContentView(int layoutResID) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        super.setContentView(wrapInBottomSheet(layoutResID, null, params));
    }

    @Override
    public void setContentView(@NonNull View view, @Nullable ViewGroup.LayoutParams params) {
        super.setContentView(wrapInBottomSheet(0, view, params));
    }

    private View wrapInBottomSheet(int layoutResId, View view, ViewGroup.LayoutParams params) {
        InnerFrameLayout innerView = new InnerFrameLayout(getContext());

        if (layoutResId != 0) {
            view = getLayoutInflater().inflate(layoutResId, innerView, true);
        }
        return innerView;
    }

    @Override
    public void show() {
        if (!mAdjustWindowHeight) {
            mAdjustWindowHeight = true;
            if (getWindow() != null) {
                WindowManager.LayoutParams p = getWindow().getAttributes();
                p.width = WindowManager.LayoutParams.MATCH_PARENT;
                p.height = WindowManager.LayoutParams.MATCH_PARENT;
            }
        }
        super.show();
    }

    private class InnerFrameLayout extends FrameLayout {

        public InnerFrameLayout(@NonNull Context context) {
            super(context);
            setBackgroundColor(ContextCompat.getColor(context, R.color.blue_pressed));
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            final int width = MeasureSpec.getSize(widthMeasureSpec);
            final int height = MeasureSpec.getSize(heightMeasureSpec);
            final DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
            if (mWidthValue.type != TypedValue.TYPE_NULL) {
                final int min;
                if (mWidthValue.type == TypedValue.TYPE_DIMENSION) {
                    min = (int) mWidthValue.getDimension(metrics);
                } else if (mWidthValue.type == TypedValue.TYPE_FRACTION) {
                    min = (int) mWidthValue.getFraction(width, width);
                } else {
                    min = 0;
                }
                if (min > 0 && getWindow() != null) {
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec(min, MeasureSpec.AT_MOST);
                }
            }

            if (mHeightValue.type != TypedValue.TYPE_NULL) {
                final int min;
                if (mHeightValue.type == TypedValue.TYPE_DIMENSION) {
                    min = (int) mHeightValue.getDimension(metrics);
                } else if (mHeightValue.type == TypedValue.TYPE_FRACTION) {
                    min = (int) mHeightValue.getFraction(height, height);
                } else {
                    min = 0;
                }
                if (min > 0 && getWindow() != null) {
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(min, MeasureSpec.AT_MOST);
                }
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(width, height);
        }
    }
}
