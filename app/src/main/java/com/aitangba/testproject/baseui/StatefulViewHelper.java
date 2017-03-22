package com.aitangba.testproject.baseui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by fhf11991 on 2017/3/21.
 */

public class StatefulViewHelper {

    private final FrameLayout.LayoutParams matchLayoutParams =
            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);

    private FrameLayout mFrameLayout;
    private View mChildView;
    private View mCoverView;

    public StatefulViewHelper(View childView) {
        mChildView = childView;
        mFrameLayout = new FrameLayout(mChildView.getContext());

        ViewGroup.LayoutParams childLayoutParams = mChildView.getLayoutParams();
        ViewGroup viewGroup = (ViewGroup)mChildView.getParent();

        viewGroup.removeView(mChildView);
        viewGroup.addView(mFrameLayout, childLayoutParams);
        mFrameLayout.addView(mChildView, matchLayoutParams);
    }

    public void showLoading() {
        if(mFrameLayout == null || mChildView == null) {
            throw new RuntimeException("You must call attachView first !");
        }

        mFrameLayout.removeAllViews();
        mFrameLayout.addView(mChildView, matchLayoutParams);

        mCoverView = getLoadingView(mFrameLayout.getContext());
        mFrameLayout.addView(mCoverView, matchLayoutParams);

        showAnimation(mCoverView);
    }

    public void dismiss() {
        if(mFrameLayout == null || mChildView == null || mCoverView == null) {
            return;
        }

        hideAnimation(mCoverView);
    }

    protected View getLoadingView(Context context) {
        View view = new View(context);
        view.setBackgroundResource(android.R.color.darker_gray);
        return view;
    }

    private void showAnimation(View coverView) {
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(coverView, View.SCALE_X, 0f, 1f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(coverView, View.SCALE_Y, 0f, 1f);
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(coverView, View.ALPHA, 0f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.playTogether(new Animator[]{scaleXAnim, scaleYAnim, alphaAnim});
        animatorSet.start();
    }

    private void hideAnimation(View coverView) {
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(coverView, View.SCALE_X, 1f, 0f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(coverView, View.SCALE_Y, 1f, 0f);
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(coverView, View.ALPHA, 1f, 0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.playTogether(new Animator[]{scaleXAnim, scaleYAnim, alphaAnim});
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mFrameLayout.removeAllViews();
                mFrameLayout.addView(mChildView, matchLayoutParams);
            }
        });
        animatorSet.start();
    }
}
