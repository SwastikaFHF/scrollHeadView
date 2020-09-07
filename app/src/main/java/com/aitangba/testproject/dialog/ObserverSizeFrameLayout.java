package com.aitangba.testproject.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Fring on 2020/9/7
 */
public class ObserverSizeFrameLayout extends FrameLayout {
    public ObserverSizeFrameLayout(@NonNull Context context) {
        super(context);
    }

    public ObserverSizeFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ObserverSizeFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                super.onSizeChanged(w, h, oldw, oldh);
                if (mOnSizeChangedListener != null) {
                    mOnSizeChangedListener.onSizeChanged(w, h);
                }
            }

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = MeasureSpec.getSize(heightMeasureSpec);
                int maxWidth = 0;
                int maxHeight = 0;
                for (int i = 0, count = getChildCount(); i < count; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != GONE) {
                        measureChild(child, MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), heightMeasureSpec);
                        if (child.getMeasuredWidth() == 0) {
                            continue;
                        }
                        int childRadio = child.getMeasuredHeight() / child.getMeasuredWidth();
                        if (childRadio * maxWidth >= maxHeight) {
                            maxWidth = child.getMeasuredWidth();
                            maxHeight = child.getMeasuredHeight();
                        }
                    }
                }

                if (maxWidth == 0 || maxHeight == 0) {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        // 子View高度不能超出我们提供的宽度
        final int targetHeight = (int) Math.ceil(maxHeight * 1f * width / maxWidth);
        if (targetHeight < height) {
            for (int i = 0, count = getChildCount(); i < count; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() != GONE) {
                    measureChild(child, MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(targetHeight, MeasureSpec.EXACTLY));
                }
            }
            setMeasuredDimension(width, targetHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    private OnSizeChangedListener mOnSizeChangedListener;

    public void setOnSizeChangedListener(OnSizeChangedListener onSizeChangedListener) {
        mOnSizeChangedListener = onSizeChangedListener;
    }

    public interface OnSizeChangedListener {
        void onSizeChanged(int w, int h);
    }
}
