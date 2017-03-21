package com.aitangba.testproject.baseui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by fhf11991 on 2017/3/21.
 */

public class StatefulHelper {

    private final FrameLayout.LayoutParams matchLayoutParams =
            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);

    private FrameLayout mFrameLayout;
    private View mChildView;

    public void attachView(View childView) {
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

        View newView = new View(mFrameLayout.getContext());
        newView.setBackgroundResource(android.R.color.darker_gray);
        mFrameLayout.addView(newView, matchLayoutParams);
    }

    public void dismiss() {
        if(mFrameLayout == null || mChildView == null) {
            return;
        }

        mFrameLayout.removeAllViews();
        mFrameLayout.addView(mChildView, matchLayoutParams);
    }
}
