package com.aitangba.testproject.view.drag;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Created by fhf11991 on 2018/8/10
 */
public class CustomScrollView extends NestedScrollView {


    private int mTouchSlop;

    public CustomScrollView(@NonNull Context context) {
        this(context, null);
    }

    public CustomScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    private boolean mIsBeingDragged;
    private float mLastMotionY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int actionMasked = event.getActionMasked();

        MarginLayoutParams params = (MarginLayoutParams) getChildAt(0).getLayoutParams();

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = event.getRawY();
                mIsBeingDragged = params.topMargin == 0;
                break;

            case MotionEvent.ACTION_MOVE:
                final float y = event.getRawY();
                final float yDiff = Math.abs(y - mLastMotionY);
                int dy = (int) (y - mLastMotionY);
                if(params.topMargin == 0) {
                    mIsBeingDragged = false;
                    break;
                }
                if (mIsBeingDragged || yDiff > mTouchSlop) {
                    mLastMotionY = y;
                    mIsBeingDragged = true;

                    if(params.topMargin + dy < 0) {
                        params.topMargin = 0;
                        mIsBeingDragged = false;
                    } else {
                        params.topMargin = params.topMargin + dy;
                    }
                    getChildAt(0).requestLayout();
                }
                break;
        }
        Log.d("CustomScrollView", "dispatchTouchEvent ---  " +
                " topMargin = " + params.topMargin
                + " ScrollY = " + getScrollY());
        return mIsBeingDragged || super.dispatchTouchEvent(event);
    }
}
