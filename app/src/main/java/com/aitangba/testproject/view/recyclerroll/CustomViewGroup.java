package com.aitangba.testproject.view.recyclerroll;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;


/**
 * Created by XBeats on 2020/4/30
 */
public class CustomViewGroup extends ViewGroup {

    private static final String TAG = "CustomView_TAG";
    private static final long LOOP_INTERVAL = 3000;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;
    private int mMaxVelocity;

    private int mCurrentVelocity;
    private int mLastX;
    private int mLastDownX;
    private int mLastDownY;
    private boolean mIsTouching;
    private boolean mAutoMoving;
    private boolean mIsBeingDragged;
    private long mLastTouchTime;

    private boolean mAutoScroll = true; // 默认开启自动滑动

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
    }

    public void enableAutoScroll(boolean enable) {
        mAutoScroll = enable;
        loop();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        loop();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dispose();
    }

    private void dispose() {
        mHandler.removeCallbacksAndMessages(null);
    }

    private void loop(){
        if(!mAutoScroll) {
            return;
        }

        if (getChildCount() <= 1) {
            return;
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mIsTouching && System.currentTimeMillis() - mLastTouchTime > LOOP_INTERVAL / 2) {
                    goNextPage();
                }
                loop();
            }
        }, LOOP_INTERVAL);
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        super.addView(child, index, params);
        loop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int maxHeight = sizeHeight - getPaddingTop() - getPaddingBottom();
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);

        int childMaxWidth = sizeWidth - getPaddingLeft() - getPaddingRight();
        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, MeasureSpec.makeMeasureSpec(childMaxWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
            height = Math.min(Math.max(height, child.getMeasuredHeight()), maxHeight);
        }
        setMeasuredDimension(sizeWidth,
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

            if (mOffset < 0) {
                if (i == mCurrentIndex) {
                    layoutChild(child, left + mOffset, top);
                } else if (i == (mCurrentIndex + 1) % getChildCount()) {
                    layoutChild(child, width + mOffset + left, top);
                } else {
                    layoutChild(child, left - width, top);
                }
            } else if (mOffset > 0) {
                if (i == (mCurrentIndex - 1 + getChildCount()) % getChildCount()) {
                    layoutChild(child, mOffset - width + left, top);
                } else if (i == mCurrentIndex) {
                    layoutChild(child, left + mOffset, top);
                } else {
                    layoutChild(child, left - width, top);
                }
            } else {
                if (i == mCurrentIndex) {
                    layoutChild(child, left + mOffset, top);
                } else {
                    layoutChild(child, left - width, top);
                }
            }
        }
    }

    private void layoutChild(View child, int left, int top) {
        child.layout(left, top, left + (getMeasuredWidth() - getPaddingLeft() - getPaddingRight()), top + child.getMeasuredHeight());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent " + actionToString(ev) + ", mIsBeingDragged = " + mIsBeingDragged);
        if(mAutoMoving) {
            return true;
        }
        int action = ev.getAction() & MotionEvent.ACTION_MASK;
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        // ACTION_DOWN 事件不进行拦截，防止子View需要处理onclick事件
        if (action == MotionEvent.ACTION_DOWN) {
            mVelocityTracker.addMovement(ev);
            mLastX = mLastDownX = (int) ev.getX();
            mLastDownY = (int) ev.getY();
            mIsTouching = true;
            mIsBeingDragged = false;
        }
        if (!mIsBeingDragged && action == MotionEvent.ACTION_MOVE) {
            float xDiff = Math.abs(ev.getX() - mLastDownX);
            if (xDiff > mTouchSlop) {
                mIsBeingDragged = true;
                requestParentDisallowInterceptTouchEvent();
            }
        }
        return mIsBeingDragged;
    }

    private void requestParentDisallowInterceptTouchEvent() {
        ViewParent parent = this.getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction() & MotionEvent.ACTION_MASK;
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        if(action != MotionEvent.ACTION_DOWN) {
            mVelocityTracker.addMovement(ev);
        }
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = mLastDownX = x;
                mLastDownY = y;
                mIsTouching = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mIsBeingDragged) {
                    float xDiff = Math.abs(x - mLastDownX);
                    if (xDiff > mTouchSlop) {
                        mIsBeingDragged = true;
                        requestParentDisallowInterceptTouchEvent();
                    }
                }

                int dx = x - mLastX;
                if(mIsBeingDragged) {
                    int targetOffset = mOffset + dx;
                    mOffset = Math.max(-getMeasuredWidth(), Math.min(getMeasuredWidth(), targetOffset));
                    requestLayout();
                    if(mOnPageChangeListener != null) {
                        mOnPageChangeListener.onPageScrolled(mCurrentIndex, mOffset);
                    }
                }
                mLastX = x;
                break;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
                handleTouchAction();
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(mLastDownX - x) < 20 && Math.abs(mLastDownY - y) < 20) {
                    return performClick();
                }
                handleTouchAction();
                break;
            default:
                mIsTouching = false;
                break;
        }
        return true;
    }

    /**
     * Velocity 向左为负值，向右为正值
     * Offset   向左为负值，向右为正值
     */
    private void handleTouchAction() {
        mVelocityTracker.computeCurrentVelocity(1000);
        mCurrentVelocity = (int) mVelocityTracker.getXVelocity();

        if (mCurrentVelocity > mMaxVelocity) {
            if(mOffset < 0) {
                back2Page();
            } else {
                goPreviousPage();
            }
        } else if (mCurrentVelocity < -mMaxVelocity) {
            if(mOffset > 0) {
                back2Page();
            } else {
                goNextPage();
            }
        } else {
            if(mOffset < - getMeasuredWidth() / 2) {
                goNextPage();
            } else if(mOffset > getMeasuredWidth() / 2) {
                goPreviousPage();
            } else {
                back2Page();
            }
        }

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
            mCurrentVelocity = 0;
        }
        mIsTouching = false;
        mLastTouchTime = System.currentTimeMillis();
    }

    /**
     * Scroll to the next position
     */
    public void goNextPage() {
        autoScroll(Direction.LEFT);
    }

    /**
     * Scroll to the previous position
     */
    public void goPreviousPage() {
        autoScroll(Direction.RIGHT);
    }

    private void back2Page() {
        autoScroll(Direction.ORIGIN);
    }

    private void autoScroll(int direction) {
        mAutoMoving = true;
        final int targetIndex;
        final int targetValue;
        if (direction == Direction.LEFT) {
            targetValue = -getMeasuredWidth();
            targetIndex = (mCurrentIndex + 1) % getChildCount();
        } else if (direction == Direction.RIGHT) {
            targetValue = getMeasuredWidth();
            targetIndex = (mCurrentIndex - 1 + getChildCount()) % getChildCount();
        } else {
            targetValue = 0;
            targetIndex = mCurrentIndex;
        }

        int dx = targetValue - mOffset;
        int width = this.getMeasuredWidth();
        int halfWidth = width / 2;
        float distanceRatio = Math.min(1.0F, 1.0F * (float)Math.abs(dx) / (float)width);
        float distance = (float)halfWidth + (float)halfWidth * this.distanceInfluenceForSnapDuration(distanceRatio);
        int duration;
        if (mCurrentVelocity > 0) {
            duration = 4 * Math.round(1000.0F * Math.abs(distance / (float) mCurrentVelocity));
        } else {
            float pageDelta = (float)Math.abs(dx) / width;
            duration = (int)((pageDelta + 1.0F) * 100.0F);
        }
        duration = Math.min(duration, 600);

        ValueAnimator animator = new ValueAnimator();
        animator.setIntValues(mOffset, targetValue);
        animator.setDuration(duration);
        animator.setInterpolator(sInterpolator);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffset = (int) animation.getAnimatedValue();
                requestLayout();
                if(mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageScrolled(mCurrentIndex, mOffset);
                }
                Log.d(TAG, "onAnimationUpdate mOffset = " + mOffset);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mCurrentIndex = targetIndex;
                mOffset = 0;
                requestLayout();
                mAutoMoving = false;
                if(mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageScrolled(mCurrentIndex, mOffset);
                }
            }
        });
        animator.start();
    }

    private float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5F;
        f *= 0.47123894F;
        return (float)Math.sin((double)f);
    }

    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            --t;
            return t * t * t * t * t + 1.0F;
        }
    };

    private int mCurrentIndex;
    private int mOffset;

    private static final class Direction {
        private static final int ORIGIN = 0;
        private static final int LEFT = 1;
        private static final int RIGHT = 2;
    }

    private static String actionToString(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
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
        }

        return "";
    }

    private OnPageChangeListener mOnPageChangeListener;

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    public interface OnPageChangeListener {
        void onPageScrolled(int position, int offset);
    }
}
