package com.aitangba.testproject.view.drag;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;

import android.util.AttributeSet;
import android.view.View;

/**
 * Created by fhf11991 on 2018/8/7
 */
public class AvatarBehavior extends CoordinatorLayout.Behavior<CircleImageView> {

    private static final String TAG = AvatarBehavior.class.getSimpleName();
    private static final float HEAD_VIEW_HEIGHT = 44F; // dp
    private static final float IMAGE_RADIUS = 40F; // dp

    private final int mHeadViewHeight;
    private final int mImageRadius;

    public AvatarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mImageRadius = (int) dp2px(context, IMAGE_RADIUS);
        mHeadViewHeight = (int) dp2px(context, HEAD_VIEW_HEIGHT);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull CircleImageView child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        if (target instanceof NestedScrollView) {
            return true;
        }
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull CircleImageView child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        refreshImageView(child, dyConsumed);
    }

    private void refreshImageView(View view, int dyConsumed) {
        final float scaleFactor = 0.4F;
        final float maxImageTranslationY = mHeadViewHeight / 2 + mImageRadius;
        final float maxImageTranslationX = (1 - scaleFactor) * mImageRadius;
        if (dyConsumed > 0) {
            float translationY = Math.abs(view.getTranslationY()) + Math.abs(dyConsumed);
            if (translationY > maxImageTranslationY) {
                translationY = maxImageTranslationY;
            }
            view.setTranslationY(-translationY);
            view.setTranslationX(-maxImageTranslationX * translationY / maxImageTranslationY);
            float scale = 1 - (1 - scaleFactor) * translationY / maxImageTranslationY;
            view.setScaleX(scale);
            view.setScaleY(scale);
        } else if (dyConsumed < 0) {
            int minTranslationY = 0;
            float translationY = Math.abs(view.getTranslationY()) - Math.abs(dyConsumed);
            if (translationY < minTranslationY) {
                translationY = minTranslationY;
            }
            view.setTranslationY(-translationY);
            view.setTranslationX(-maxImageTranslationX * translationY / maxImageTranslationY);
            float scale = 1 - (1 - scaleFactor) * translationY / maxImageTranslationY;
            view.setScaleX(scale);
            view.setScaleY(scale);
        }
    }

    private static float dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5F;
    }
}
