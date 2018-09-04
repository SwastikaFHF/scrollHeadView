package com.aitangba.testproject.view.drag;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;

/**
 * Created by fhf11991 on 2018/8/10
 */
public class CustomScrollView extends NestedScrollView {

    private static final String TAG = "CustomScrollView";
    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private final int mMaxTopMargin;

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
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

        mMaxTopMargin = (int) dp2px(context, 100);
    }

    private static float dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5F;
    }

    private float mLastMotionY;
    private boolean mIsBeingDragged;
    private final int mMinTopMargin = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        Log.d(TAG, "onInterceptTouchEvent --- " + actionToString(event.getAction()));

        final int actionMasked = event.getActionMasked();
        final int action = event.getAction();

        if ((action == MotionEvent.ACTION_MOVE) && (mIsBeingDragged)) {
            mIsBeingDragged = true;
        } else {
            switch (actionMasked) {
                case MotionEvent.ACTION_DOWN:
                    mIsBeingDragged = false;
                    break;

                case MotionEvent.ACTION_MOVE:
                    final float y = event.getRawY();
                    final float yDiff = Math.abs(y - mLastMotionY);
                    if (yDiff > mTouchSlop) {
                        mIsBeingDragged = true;
                        requestDisallowInterceptTouchEvent();
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    mIsBeingDragged = false;
                    break;
            }
        }

        return super.onInterceptTouchEvent(event) || mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent --- " + actionToString(event.getAction()));

        final int actionMasked = event.getActionMasked();
        MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = event.getRawY();
                requestDisallowInterceptTouchEvent();
                break;

            case MotionEvent.ACTION_MOVE:
                final float y = event.getRawY();

                if(!mIsBeingDragged) {
                    if (Math.abs(y - mLastMotionY) > mTouchSlop) {
                        mIsBeingDragged = true;
                        requestDisallowInterceptTouchEvent();
                    }
                }

                if (mIsBeingDragged) { // 开始处理拖拽事件
                    int dy = (int) (y - mLastMotionY);
                    mLastMotionY = y;

                    int minTopMargin = mMinTopMargin;
                    View view = this;
                    int dyConsumed;
                    if (params.topMargin == minTopMargin) {
                        if (dy <= 0) { // 上拉
                            dyConsumed = 0;
                            mIsBeingDragged = false;
                        } else {      // 下拉
                            if (view.getScrollY() != 0) {
                                dyConsumed = 0;
                                mIsBeingDragged = false;
                            } else {
                                dyConsumed = dy;
                            }
                        }
                    } else if (params.topMargin == mMaxTopMargin) {
                        if (dy < 0) { // 上拉
                            dyConsumed = dy;
                        } else {      // 下拉
                            dyConsumed = 0;
                            mIsBeingDragged = false;
                        }
                    } else {
                        if (params.topMargin + dy < minTopMargin) {
                            dyConsumed = minTopMargin - params.topMargin;
                        } else if (params.topMargin + dy > mMaxTopMargin) {
                            dyConsumed = mMaxTopMargin - params.topMargin;
                        } else {
                            dyConsumed = dy;
                        }
                    }
                    Log.d(TAG, "onTouchEvent --- " + actionToString(event.getAction())
                            + "  topMargin = " + params.topMargin
                            + "  dy = " + dy
                            + "  dyConsumed = " + dyConsumed
                            + "  getScrollY = " + getScrollY()
                    );

                    return dispatchDragEvent(params, dyConsumed) || mIsBeingDragged || super.onTouchEvent(event);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                break;
        }
        Log.d(TAG, "onTouchEvent --- " + actionToString(event.getAction())
                + "  getScrollY = " + getScrollY()
                + "  topMargin = " + params.topMargin
        );
        return super.onTouchEvent(event);
    }

    private boolean dispatchDragEvent(MarginLayoutParams params, int dyConsumed) {
        final int originTopMargin = params.topMargin;
        params.topMargin = (int) (originTopMargin + (dyConsumed > 0 ? 1 : -1) * Math.ceil(1d * Math.abs(dyConsumed) / 2)); // 阻尼系数为0.5
        params.topMargin = Math.max(mMinTopMargin, params.topMargin);
        params.topMargin = Math.min(mMaxTopMargin, params.topMargin);
        requestLayout();
        return dyConsumed != 0;
    }

    private void requestDisallowInterceptTouchEvent() {
        final ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
    }

    private String actionToString(int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                return "ACTION_DOWN";
            case MotionEvent.ACTION_UP:
                return "ACTION_UP";
            case MotionEvent.ACTION_CANCEL:
                return "ACTION_CANCEL";
            case MotionEvent.ACTION_OUTSIDE:
                return "ACTION_OUTSIDE";
            case MotionEvent.ACTION_MOVE:
                return "ACTION_MOVE";
            case MotionEvent.ACTION_HOVER_MOVE:
                return "ACTION_HOVER_MOVE";
            case MotionEvent.ACTION_SCROLL:
                return "ACTION_SCROLL";
            case MotionEvent.ACTION_HOVER_ENTER:
                return "ACTION_HOVER_ENTER";
            case MotionEvent.ACTION_HOVER_EXIT:
                return "ACTION_HOVER_EXIT";
            case MotionEvent.ACTION_BUTTON_PRESS:
                return "ACTION_BUTTON_PRESS";
            case MotionEvent.ACTION_BUTTON_RELEASE:
                return "ACTION_BUTTON_RELEASE";
            default:
                return "UNKNOWN";
        }
    }
}
