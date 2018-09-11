package com.aitangba.testproject.view.drag;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;

/**
 * Created by fhf11991 on 2018/8/10
 */
public class CustomScrollView extends NestedScrollView {

    private static final String TAG = "CustomScrollView";
    private final int mTouchSlop;

    private int mMinTopMargin;
    private int mMaxTopMargin;

    private boolean mIsBeingDragged;
    private boolean mIsMarginEvent;
    private float mLastMotionY;

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

        mMaxTopMargin = 0;
        mMaxTopMargin = 0;
    }

    public void setMinTopMargin(int minTopMargin) {
        mMinTopMargin = minTopMargin;
    }

    public void setMaxTopMargin(int maxTopMargin) {
        mMaxTopMargin = maxTopMargin;
    }

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
                    mLastMotionY = event.getRawY();
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

        return mIsBeingDragged || super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int actionMasked = event.getActionMasked();
        MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = event.getRawY();
                mIsMarginEvent = false;
                requestDisallowInterceptTouchEvent();
                break;

            case MotionEvent.ACTION_MOVE:
                final float y = event.getRawY();
                if (!mIsBeingDragged) {
                    if (Math.abs(y - mLastMotionY) > mTouchSlop) {
                        mIsBeingDragged = true;
                    }
                }
                if (mIsBeingDragged) {
                    requestDisallowInterceptTouchEvent();

                    final int dy = (int) (y - mLastMotionY);
                    if (dy > 0) { // 下拉
                        // 下发滑动事件
                        if (getScrollY() > 0) {
                            mLastMotionY = y;
                            mIsMarginEvent = false;
                            break;
                        }

                        // 处理拖拽事件
                        if (params.topMargin >= mMinTopMargin && params.topMargin < mMaxTopMargin) {
                            dispatchDragEvent(params, dy);
                            mLastMotionY = y;
                            mIsMarginEvent = true;
                            return true;
                        }

                        if (mIsMarginEvent) { // 拖拽转换为滑动事件，需要重置源码中的mLastMotionY
                            MotionEvent motionEvent = MotionEvent.obtain(event);
                            motionEvent.setAction(MotionEvent.ACTION_DOWN);
                            super.onTouchEvent(motionEvent);
                            mLastMotionY = y;
                            mIsMarginEvent = false;
                            return true;
                        }

                        // 边缘事件
                        mLastMotionY = y;
                        mIsMarginEvent = false;
                        break;
                    } else if (dy < 0) { // 上拉
                        // 处理拖拽事件
                        if (params.topMargin > mMinTopMargin && params.topMargin <= mMaxTopMargin) {
                            dispatchDragEvent(params, dy);
                            mLastMotionY = y;
                            mIsMarginEvent = true;
                            return true;
                        }

                        if (mIsMarginEvent) { // 拖拽转换为滑动事件，需要重置源码中的mLastMotionY
                            MotionEvent motionEvent = MotionEvent.obtain(event);
                            motionEvent.setAction(MotionEvent.ACTION_DOWN);
                            super.onTouchEvent(motionEvent);
                            mLastMotionY = y;
                            mIsMarginEvent = false;
                            return true;
                        }
                        // 滑动事件和边缘事件
                        mLastMotionY = y;
                        mIsMarginEvent = false;
                        break;
                    } else {
                        return true;
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                mIsMarginEvent = false;
                if (mOnScrollEndListener != null) {
                    mOnScrollEndListener.onStop(this);
                }
                break;
        }
        Log.d(TAG, "onTouchEvent --- " + actionToString(event.getAction())
                + "  topMargin = " + params.topMargin
                + "  getScrollY = " + getScrollY()
        );
        return super.onTouchEvent(event);
    }

    private void dispatchDragEvent(MarginLayoutParams params, int dyConsumed) {
        final int originTopMargin = params.topMargin;
        params.topMargin = (int) (originTopMargin + (dyConsumed > 0 ? 1 : -1) * Math.ceil(1d * Math.abs(dyConsumed) / 2)); // 阻尼系数为0.5
        params.topMargin = Math.max(mMinTopMargin, params.topMargin);
        params.topMargin = Math.min(mMaxTopMargin, params.topMargin);

        requestLayout();

        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(this, params.topMargin, dyConsumed);
        }
    }

    private void requestDisallowInterceptTouchEvent() {
        final ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
    }

    private OnScrollListener mOnScrollListener;

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    public interface OnScrollListener {
        void onScroll(CustomScrollView scrollView, int topMargin, int dy);
    }

    private OnScrollEndListener mOnScrollEndListener;

    public void setOnScrollEndListener(OnScrollEndListener onScrollEndListener) {
        mOnScrollEndListener = onScrollEndListener;
    }

    public interface OnScrollEndListener {
        void onStop(CustomScrollView scrollView);
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
