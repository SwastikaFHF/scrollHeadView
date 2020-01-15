package com.aitangba.testproject.html;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.OverScroller;

/**
 * Created by XBeats on 2020/1/13
 */
public class ScrollableTextView extends AppCompatTextView {
    private static final int INVALID_POINTER = -1;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SETTLING = 2;

    private int mScrollState = SCROLL_STATE_IDLE;
    private int mScrollPointerId = INVALID_POINTER;
    private VelocityTracker mVelocityTracker;
    private int mLastTouchY;
    private int mTouchSlop;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;
    private final ViewFling mViewFling = new ViewFling();
    private int mContentHeight;

    //f(x) = (x-1)^5 + 1
    private static final Interpolator sQuinticInterpolator = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    public ScrollableTextView(Context context) {
        this(context, null);
    }

    public ScrollableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        final ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        mContentHeight = getMeasuredHeight();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        boolean eventAddedToVelocityTracker = false;
        final int action = MotionEventCompat.getActionMasked(event);
        final int actionIndex = MotionEventCompat.getActionIndex(event);
        final MotionEvent motionEvent = MotionEvent.obtain(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                setScrollState(SCROLL_STATE_IDLE);
                mScrollPointerId = event.getPointerId(0);
                mLastTouchY = (int) (event.getY() + 0.5f);
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                mScrollPointerId = event.getPointerId(actionIndex);
                mLastTouchY = (int) (event.getY(actionIndex) + 0.5f);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final int index = event.findPointerIndex(mScrollPointerId);
                if (index < 0) {
                    return false;
                }

                final int y = (int) (event.getY(index) + 0.5f);
                int dy = mLastTouchY - y;

                if (mScrollState != SCROLL_STATE_DRAGGING) {
                    if (Math.abs(dy) > mTouchSlop) {
                        setScrollState(SCROLL_STATE_DRAGGING);
                    }
                }

                if (mScrollState == SCROLL_STATE_DRAGGING) {
                    mLastTouchY = y;
                    constrainScrollBy(0, dy);
                }

                break;
            }
            case MotionEventCompat.ACTION_POINTER_UP: {
                if (event.getPointerId(actionIndex) == mScrollPointerId) {
                    // Pick a new pointer to pick up the slack.
                    final int newIndex = actionIndex == 0 ? 1 : 0;
                    mScrollPointerId = event.getPointerId(newIndex);
                    mLastTouchY = (int) (event.getY(newIndex) + 0.5f);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                mVelocityTracker.addMovement(motionEvent);
                eventAddedToVelocityTracker = true;
                mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                float yVelocity = -VelocityTrackerCompat.getYVelocity(mVelocityTracker, mScrollPointerId);
                if (Math.abs(yVelocity) < mMinFlingVelocity) {
                    yVelocity = 0F;
                } else {
                    yVelocity = Math.max(-mMaxFlingVelocity, Math.min(yVelocity, mMaxFlingVelocity));
                }
                if (yVelocity != 0) {
                    mViewFling.fling((int) yVelocity);
                } else {
                    setScrollState(SCROLL_STATE_IDLE);
                }
                resetTouch();
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                resetTouch();
                break;
            }
        }
        if (!eventAddedToVelocityTracker) {
            mVelocityTracker.addMovement(motionEvent);
        }
        motionEvent.recycle();
        return true;

    }

    private void resetTouch() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }
    }

    private void setScrollState(int state) {
        if (state == mScrollState) {
            return;
        }
        mScrollState = state;
        if (state != SCROLL_STATE_SETTLING) {
            mViewFling.stop();
        }
    }

    private class ViewFling implements Runnable {

        private int mLastFlingY = 0;
        private OverScroller mScroller;
        private boolean mEatRunOnAnimationRequest = false;
        private boolean mReSchedulePostAnimationCallback = false;

        public ViewFling() {
            mScroller = new OverScroller(getContext(), sQuinticInterpolator);
        }

        @Override
        public void run() {
            disableRunOnAnimationRequests();
            final OverScroller scroller = mScroller;
            if (scroller.computeScrollOffset()) {
                final int y = scroller.getCurrY();
                int dy = y - mLastFlingY;
                mLastFlingY = y;
                constrainScrollBy(0, dy);
                postOnAnimation();
            }
            enableRunOnAnimationRequests();
        }

        public void fling(int velocityY) {
            mLastFlingY = 0;
            setScrollState(SCROLL_STATE_SETTLING);
            mScroller.fling(0, 0, 0, velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            postOnAnimation();
        }

        public void stop() {
            removeCallbacks(this);
            mScroller.abortAnimation();
        }

        private void disableRunOnAnimationRequests() {
            mReSchedulePostAnimationCallback = false;
            mEatRunOnAnimationRequest = true;
        }

        private void enableRunOnAnimationRequests() {
            mEatRunOnAnimationRequest = false;
            if (mReSchedulePostAnimationCallback) {
                postOnAnimation();
            }
        }

        void postOnAnimation() {
            if (mEatRunOnAnimationRequest) {
                mReSchedulePostAnimationCallback = true;
            } else {
                removeCallbacks(this);
                ViewCompat.postOnAnimation(ScrollableTextView.this, this);
            }
        }
    }

    private void constrainScrollBy(int dx, int dy) {
        final int maxDy = mContentHeight - getMeasuredHeight();
        final int targetDy = getScrollY() + dy;

        if (targetDy > maxDy) { // 下边界
            scrollTo(0, maxDy);
        } else if (targetDy < 0) { // 上边界
            scrollTo(0, 0);
        } else {
            scrollBy(dx, dy);
        }
    }
}
