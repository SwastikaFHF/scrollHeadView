package com.aitangba.testproject.view.drag;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by fhf11991 on 2018/8/10
 */
public class TextBehavior extends CoordinatorLayout.Behavior<ObserverSizeTextView> {

    private static final String TAG = TextBehavior.class.getSimpleName();
    private static final float HEAD_VIEW_HEIGHT = 44F; // dp
    private static final float IMAGE_RADIUS = 40F; // dp

    private final int mHeadViewHeight;
    private final int mImageRadius;

    public TextBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mImageRadius = (int) dp2px(context, IMAGE_RADIUS);
        mHeadViewHeight = (int) dp2px(context, HEAD_VIEW_HEIGHT);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull ObserverSizeTextView child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        if (target instanceof NestedScrollView) {
            return true;
        }
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull ObserverSizeTextView child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        refreshTextView(coordinatorLayout, child, dyConsumed);
    }

    private void refreshTextView(ViewGroup parent, View view, int dyConsumed) {
        final float scaleFactor = 0.8F;
        final float maxTextTranslationY = mHeadViewHeight / 2 + mImageRadius;
        final int width = view.getMeasuredWidth();
        ViewGroup.MarginLayoutParams textLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        final float maxTextTranslationX = parent.getMeasuredWidth() / 2 - textLayoutParams.leftMargin - width / 2 - (1 - scaleFactor) / 2 * width;

        if (dyConsumed > 0) {
            float translationY = Math.abs(view.getTranslationY()) + Math.abs(dyConsumed);
            if (translationY > maxTextTranslationY) {
                translationY = maxTextTranslationY;
            }
            view.setTranslationY(-translationY);
            view.setTranslationX(maxTextTranslationX * translationY / maxTextTranslationY);
            float scale = 1 - (1 - scaleFactor) * translationY / maxTextTranslationY;
            view.setScaleX(scale);
            view.setScaleY(scale);
        } else if (dyConsumed < 0) {
            int minTranslationY = 0;
            float translationY = Math.abs(view.getTranslationY()) - Math.abs(dyConsumed);
            if (translationY < minTranslationY) {
                translationY = minTranslationY;
            }
            view.setTranslationY(-translationY);
            view.setTranslationX(maxTextTranslationX * translationY / maxTextTranslationY);
            float scale = 1 - (1 - scaleFactor) * translationY / maxTextTranslationY;
            view.setScaleX(scale);
            view.setScaleY(scale);
        }
    }

    private static float dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5F;
    }
}
