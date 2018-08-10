package com.aitangba.testproject.view.drag;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by fhf11991 on 2018/8/10
 */
public class ScrollBehavior extends CoordinatorLayout.Behavior<NestedScrollView> {

    private static final String TAG = ScrollBehavior.class.getSimpleName();

    public ScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull NestedScrollView child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        if (target instanceof android.support.v4.widget.NestedScrollView) {
            return true;
        }
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull NestedScrollView child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
        params.topMargin = params.topMargin - dyConsumed;
        child.requestLayout();

        Log.d(TAG, "onNestedScroll -- dyConsumed = " + dyConsumed);
    }
}
