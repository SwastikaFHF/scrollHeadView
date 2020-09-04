package com.aitangba.testproject.view.coordinatorlayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;


/**
 * Created By XBeats on 2019/1/2
 */
public class BodyBehavior extends CoordinatorLayout.Behavior<NestedScrollView> {

    private static final String TAG = "BodyBehavior";
    private final TimeInterpolator mTimeInterpolator = new DecelerateInterpolator(2f);
    private View mBodyLayout;
    private int mOriginHeight;
    private final int mMaxScrollY;
    private ValueAnimator mAnimator;

    public BodyBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMaxScrollY = 0;
    }

    private boolean mNestedScrolling;

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull NestedScrollView child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        Log.d(TAG, "onStartNestedScroll --- is Nested Scrolling = " + mNestedScrolling);
        // 过滤重复滑动事件
        if (mNestedScrolling) {
            return false;
        }
        return mNestedScrolling = target instanceof NestedScrollView && !target.canScrollVertically(-1);
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull NestedScrollView child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        if (child.getScrollY() != 0) {
            return;
        }
        if (mBodyLayout == null) {
//            mBodyLayout = child.findViewById(R.id.bodyLayout);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mBodyLayout.getLayoutParams();
            mOriginHeight = params.topMargin;
        }

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mBodyLayout.getLayoutParams();
        final int topMargin = layoutParams.topMargin;
        if (topMargin > mOriginHeight || dy < 0) {
            int targetHeight = topMargin - dy / 2;
            if (targetHeight <= mOriginHeight) {
                layoutParams.topMargin = mOriginHeight;
            } else if (targetHeight <= mOriginHeight + mMaxScrollY) {
                layoutParams.topMargin = targetHeight;
            } else {
                layoutParams.topMargin = mOriginHeight + mMaxScrollY;
            }
            mBodyLayout.requestLayout();

            consumed[1] = dy;
        }
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull NestedScrollView child, @NonNull View target, int type) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type);
        if (mBodyLayout != null && mAnimator == null) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mBodyLayout.getLayoutParams();
            int distance = layoutParams.topMargin - mOriginHeight;
            Log.d(TAG, "onStopNestedScroll --- and play animator, distance = " + distance);
            // 再次过滤无效滑动
            if(distance == 0) {
                mAnimator = null;
                mNestedScrolling = false;
                return;
            }
            mAnimator = ValueAnimator.ofInt(layoutParams.topMargin, mOriginHeight);
            mAnimator.setInterpolator(mTimeInterpolator);
            mAnimator.setDuration(400 * Math.abs(layoutParams.topMargin - mOriginHeight) / mMaxScrollY);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = (Integer) animation.getAnimatedValue();
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mBodyLayout.getLayoutParams();
                    layoutParams.topMargin = value;
                    mBodyLayout.requestLayout();
                }
            });
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    Log.d(TAG, "onAnimationEnd --- ");
                    mAnimator = null;
                    mNestedScrolling = false;
                }
            });
            mAnimator.start();
        }
    }
}
