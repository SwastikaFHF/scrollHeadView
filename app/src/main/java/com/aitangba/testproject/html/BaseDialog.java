package com.aitangba.testproject.html;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.aitangba.testproject.R;

/**
 * Created by XBeats on 2020/1/10
 */
public class BaseDialog extends Dialog {

    private final TypedValue mWidthValue = new TypedValue();
    private final TypedValue mHeightValue = new TypedValue();
    private int mGravity;
    private boolean mBackgroundDimEnabled;

    public BaseDialog(@NonNull Context context) {
        this(context, 0);
        init();
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    private void init() {
        getContext().getTheme().resolveAttribute(R.attr.windowWidth, mWidthValue, true);
        getContext().getTheme().resolveAttribute(R.attr.windowHeight, mHeightValue, true);

        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.layout_gravity, android.R.attr.backgroundDimEnabled});
        mGravity = typedArray.getInt(0, Gravity.NO_GRAVITY);
        mBackgroundDimEnabled = typedArray.getBoolean(1, true);
        typedArray.recycle();
    }

    @Override
    public void setContentView(@NonNull View view) {
        super.setContentView(wrapInBottomSheet(0, view, null), getLayoutParams());
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(wrapInBottomSheet(layoutResID, null, null), getLayoutParams());
    }

    @Override
    public void setContentView(@NonNull View view, @Nullable ViewGroup.LayoutParams params) {
        super.setContentView(wrapInBottomSheet(0, view, params), getLayoutParams());
    }

    private View wrapInBottomSheet(int layoutResId, View view, ViewGroup.LayoutParams params) {
        InnerFrameLayout innerView = new InnerFrameLayout(getContext());

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
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = mGravity;
        return layoutParams;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            if (!mBackgroundDimEnabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.setStatusBarColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
            }

            WindowManager.LayoutParams windowAttributes = window.getAttributes();
            windowAttributes.width = WindowManager.LayoutParams.MATCH_PARENT;
            windowAttributes.height = WindowManager.LayoutParams.MATCH_PARENT;
        }
    }

    private class InnerFrameLayout extends FrameLayout {

        public InnerFrameLayout(@NonNull Context context) {
            super(context);
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

            int maxHeight = 0;
            int maxWidth = 0;
            for (int i = 0, count = getChildCount(); i < count; i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() != GONE) {
                    measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    maxWidth = Math.max(maxWidth,
                            child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                    maxHeight = Math.max(maxHeight,
                            child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
                }
            }

            setMeasuredDimension(maxWidth, maxHeight);
        }
    }
}
