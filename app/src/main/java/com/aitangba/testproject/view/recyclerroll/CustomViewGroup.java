package com.aitangba.testproject.view.recyclerroll;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by XBeats on 2020/4/30
 */
public class CustomViewGroup extends ViewGroup {

    private static final String TAG = "CustomView_TAG";
    private int mTouchSlop;

    public CustomViewGroup(Context context) {
        this(context, null);
    }

    public CustomViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ViewConfiguration config = ViewConfiguration.get(context);
        mMaxVelocity = config.getScaledMinimumFlingVelocity();
        mTouchSlop = config.getScaledTouchSlop();
        mScroller = new Scroller(context);
    }

    private int mMaxVelocity;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int maxHeight = sizeHeight - getPaddingTop() - getPaddingBottom();
        int childMaxWidth = (int) ((MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight()));
        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, MeasureSpec.makeMeasureSpec(childMaxWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
            height = Math.min(Math.max(height, child.getMeasuredHeight()), maxHeight);
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() == 0) {
            return;
        }

        int left = getPaddingLeft();
        int top = getPaddingTop();
        int width = getMeasuredWidth();
        for (int i = 0, count = getChildCount(); i < count; i++) {
            View child = getChildAt(i);
            if (i == currentIndex) {
                layoutChild(child, left, top);
            } else if (i == (currentIndex + 1) % count) {
                if (direction == Direction.RIGHT) {
                    layoutChild(child, width, top);
                } else {
                    layoutChild(child, -width, top);
                }
            } else {
                layoutChild(child, -width, top);
            }
        }
    }

    private void layoutChild(View child, int left, int top) {
        child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
    }

    private VelocityTracker mVelocityTracker;
    private Scroller mScroller;
    private int mLastX, mLastDownX, mLastDownY;
    private boolean mIsTouching;
    private boolean mIsBeingDragged;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction() & MotionEvent.ACTION_MASK;
        if (action != MotionEvent.ACTION_UP && action != MotionEvent.ACTION_CANCEL) {
            if (mIsBeingDragged) {
                return true;
            }
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = mLastDownX = (int) ev.getX();
                mLastDownY = (int) ev.getY();
                mIsTouching = true;
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) (mLastX - ev.getX());
                float xDiff = Math.abs(dx);
                if (xDiff > mTouchSlop) {
                    mIsBeingDragged = true;
                    requestDisallowInterceptTouchEvent(true);
                }
                break;
        }
        return mIsBeingDragged;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isManuallyScrollLocked()) {
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            }
            mVelocityTracker.addMovement(ev);
        }
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished() && !isManuallyScrollLocked()) {
                    mScroller.abortAnimation();
                }
                mLastX = mLastDownX = x;
                mLastDownY = y;
                mIsTouching = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isManuallyScrollLocked()) {
                    int dx = mLastX - x;

                    if (getScrollX() <= 0 && getScrollX() + dx > 0) {
                        direction = Direction.RIGHT;
                        requestLayout();
                        scrollBy(dx + getScrollX(), 0);
                    } else if (getScrollX() >= 0 && getScrollX() + dx < 0) {
                        direction = Direction.LEFT;
                        requestLayout();
                        scrollBy(dx + getScrollX(), 0);
                    } else {
                        scrollBy(dx, 0);
                    }
                    if (Math.abs(mLastDownX - x) > 20) {
                        requestDisallowInterceptTouchEvent(true);
                    }
                }
                mLastX = x;
                break;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
                if (x != mLastDownX) {
                    slowScrollToPage();
                }
                touchFinished();
                break;
            case MotionEvent.ACTION_UP:
                float outsideEachPercent = 0;
                if (Math.abs(mLastDownX - x) < 20
                        && Math.abs(mLastDownY - y) < 20
                        && !performClick()) {
                    if (x > getWidth() * outsideEachPercent && x < getWidth() * (1 - outsideEachPercent)) {
                    } else if (!isManuallyScrollLocked()) {
                        if (x < getWidth() * outsideEachPercent) {
                            goPreviousPage();
                        } else {
                            goNextPage();
                        }
                    }
                } else if (!isManuallyScrollLocked()) {
                    handleScroll();
                }
                touchFinished();
                break;
            default:
                mIsTouching = false;
                break;
        }
        return true;
    }

    private boolean autoSlide;
    @Override
    public void computeScroll() {
        int scrollX = mScroller.getCurrX();
        Log.d(TAG, "scrollX = " + scrollX + ", computeScrollOffset = "+ mScroller.computeScrollOffset());
        if (mScroller.computeScrollOffset()) {
            if(autoSlide && scrollX == 1440) {
                requestLayout();
                mScroller.abortAnimation();
                scrollTo(0, 0);
                currentIndex = (currentIndex + getChildCount() - 1) % getChildCount();
                direction = Direction.ORIGIN;
                Log.d(TAG, "currentIndex = " + currentIndex);
                return;
            }
            scrollTo(scrollX, mScroller.getCurrY());

            invalidate();
        }
    }

    private boolean isManuallyScrollLocked() {
        return getChildCount() <= 1;
    }

    private void slowScrollToPage() {
        int scrollX = getScrollX();
        int width = getMeasuredWidth();

        if (scrollX < -width / 2) {
            goPreviousPage();
        } else if (scrollX > width / 2) {
            goNextPage();
        } else {
            scrollToPage(Direction.ORIGIN);
        }
    }

    private long mLastTouchTime;

    private void touchFinished() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
        mIsTouching = false;
        mLastTouchTime = System.currentTimeMillis();
    }

    private void handleScroll() {
        autoSlide = true;
        mVelocityTracker.computeCurrentVelocity(1000);
        int initVelocity = (int) mVelocityTracker.getXVelocity();
        if (initVelocity > mMaxVelocity) {
            goPreviousPage();
        } else if (initVelocity < -mMaxVelocity) {
            goNextPage();
        } else {
            slowScrollToPage();
        }
    }

    /**
     * Scroll to the next position
     */
    public void goNextPage() {
        scrollToPage(Direction.RIGHT);
    }

    /**
     * Scroll to the previous position
     */
    public void goPreviousPage() {
        scrollToPage(Direction.LEFT);
    }

    private void scrollToPage(int direction) {
        int dx;
        if (direction == Direction.RIGHT) {
            dx = getMeasuredWidth() - getScrollX();
        } else if (direction == Direction.LEFT) {
            dx = -getMeasuredWidth() - getScrollX();
        } else {
            dx = -getScrollX();
        }

        mScroller.startScroll(getScrollX(), 0, dx, 0, Math.abs(dx));
        invalidate();
    }


    private int currentIndex;

    private int direction = Direction.RIGHT; // 默右滑

    private static final class Direction {
        private static final int ORIGIN = 0;
        private static final int LEFT = 1;
        private static final int RIGHT = 2;
    }
}
