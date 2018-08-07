package com.aitangba.testproject.view.drag;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewParentCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by fhf11991 on 2018/8/6
 */
public class DragFrameLayout extends FrameLayout implements NestedScrollingParent {

    private static final String TAG = "DragFrameLayout";
    private ImageView mImageView;
    private TextView mTextView;

    public DragFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public DragFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bindImageView(ImageView imageView) {
        mImageView = imageView;
    }

    public void bindTextView(TextView textView) {
        mTextView = textView;
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return target instanceof NestedScrollView;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.d(TAG, "onNestedScroll --- "
                + "  dxConsumed = " + dxConsumed
                + "  dyConsumed = " + dyConsumed
                + "  dxUnconsumed = " + dxUnconsumed
                + "  dyUnconsumed = " + dyUnconsumed);
        if (mImageView != null) {
            if (dyConsumed > 0) {
                if (mImageView.getTranslationY() < -100) {
                } else {
                    float translationY = mImageView.getTranslationY() - dyConsumed;
                    mImageView.setTranslationY(translationY);
                    float scale = Math.max(0.5f, 0.5f * (1 - translationY / dp2px(getContext(), 78)));
                    mImageView.setScaleX(scale);
                    mImageView.setScaleY(scale);
                }
            } else {
                if (mImageView.getTranslationY() >= 0) {
                } else {
                    float translationY = mImageView.getTranslationY() - dyConsumed;
                    mImageView.setTranslationY(translationY);
                    float scale = Math.min(1, 0.5f * (1 + translationY / dp2px(getContext(), 78)));
                    mImageView.setScaleX(scale);
                    mImageView.setScaleY(scale);
                }
            }
        }

        if (mTextView != null) {
            if (dyUnconsumed < 0 && dxConsumed == 0) { // 下拉到顶
                mTextView.setTranslationY(mTextView.getTranslationY() + Math.abs(dyUnconsumed));
                return;
            }
        }
    }

    @Override
    public void onStopNestedScroll(View child) {
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5F);
    }
}
