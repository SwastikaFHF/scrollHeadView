package com.aitangba.testproject.view.drag;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by fhf11991 on 2018/8/7
 */
public class AvatarBehavior extends CoordinatorLayout.Behavior<CircleImageView> {

    private static final String TAG = AvatarBehavior.class.getSimpleName();

    public AvatarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        if(dyConsumed > 0) {
            if(child.getTranslationY() < -100) {
                return;
            } else {
                child.setTranslationY(child.getTranslationY() - dyConsumed);
            }
        } else {
            if(child.getTranslationY() >= 0) {
                return;
            } else {
                child.setTranslationY(child.getTranslationY() - dyConsumed);
            }
        }
        Log.d(TAG, "onNestedScroll --  dxConsumed = " + dxConsumed
                + " dyConsumed = " + dyConsumed
                + " dxUnconsumed = " + dxUnconsumed
                + " dyUnconsumed = " + dyUnconsumed);
    }
}
