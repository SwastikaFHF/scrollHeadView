package com.aitangba.testproject.view.nestwebview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import androidx.core.view.NestedScrollingChild2;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.WebView;
import android.widget.OverScroller;

import java.util.Locale;

/**
 * Created by XBeats on 2020/7/30
 */
public class NestWebView extends WebView implements NestedScrollingChild2 {

    private static final String TAG = "NestWebView_TAG";

    private final int[] mScrollConsumed = new int[2];
    private final int[] mScrollOffset = new int[2];

    private final NestedScrollingChildHelper mChildHelper;
    private final int mMinimumVelocity;
    private final int mMaximumVelocity;

    private VelocityTracker mVelocityTracker;
    private OverScroller mScroller;
    private int mLastMotionY;
    private int mLastScrollerY;
    private int mNestedYOffset;

    public NestWebView(Context context) {
        this(context, null);
    }

    public NestWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);

        mScroller = new OverScroller(getContext());

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

        addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                getCurrentHeight();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                mHandler.removeCallbacksAndMessages(null);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initVelocityTrackerIfNotExists();
        MotionEvent motionEvent = MotionEvent.obtain(event);

        final int actionMasked = event.getAction();

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                requestDisallowInterceptTouchEvent(true);
                mLastMotionY = (int) event.getY();
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                mVelocityTracker.addMovement(motionEvent);
                mScroller.computeScrollOffset();
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_UP:
                stopNestedScroll();
                mNestedYOffset = 0;
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int initialVelocity = (int) velocityTracker.getYVelocity();
                if (Math.abs(initialVelocity) > mMinimumVelocity) {
                    fling(-initialVelocity);
                }
                recycleVelocityTracker();
                requestDisallowInterceptTouchEvent(false);
            case MotionEvent.ACTION_CANCEL:
                stopNestedScroll();
                recycleVelocityTracker();
                requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_MOVE:

                mVelocityTracker.addMovement(motionEvent);
                final int y = (int) event.getY() + mNestedYOffset;
                final int deltaY = y - mLastMotionY;

                mLastMotionY = y;
                int scrollY = getScrollY();

                // 预处理滑动事件
                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    motionEvent.offsetLocation(0, mScrollConsumed[1]);
                    mNestedYOffset += mScrollOffset[1];
                }

                // 向上滑动，优先查看Parent要不要处理，
                // 向下滑动，优先查看自己要不要处理
                if (scrollY == 0) {
                    dispatchNestedScroll(0, 0, 0, -deltaY, mScrollOffset);
                    mNestedYOffset += mScrollOffset[1];

                    log(String.format(Locale.CHINESE, "scrollY = 0: mScrollOffset[1] = %d, dy = %d ", mScrollOffset[1], deltaY));
                    if (deltaY != 0 && mScrollOffset[1] == 0) {
                        motionEvent.offsetLocation(0, mScrollOffset[1]);
                        return super.onTouchEvent(event);
                    }
                    return true;
                } else if (scrollY < (int) (getContentHeight() * getScale() - getHeight())) {
                    log(String.format(Locale.CHINESE, "scrollY > 0: mScrollOffset[1] = %d: ", mScrollOffset[1]));
                    return super.onTouchEvent(event);
                } else {
                    log(String.format(Locale.CHINESE, "scrollY = max: mScrollOffset[1] = %d: ", mScrollOffset[1]));
                    if (deltaY > 0) {
                        return super.onTouchEvent(event);
                    } else {
                        dispatchNestedScroll(0, 0, 0, -deltaY, mScrollOffset);
                        mNestedYOffset += mScrollOffset[1];
                        return true;
                    }
                }
            default:
                break;
        }
        return super.onTouchEvent(motionEvent);
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void fling(int velocityY) {
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH);
        mScroller.fling(getScrollX(), getScrollY(), // start
                0, velocityY, // velocities
                0, 0, // x
                Integer.MIN_VALUE, Integer.MAX_VALUE, // y
                0, 0); // overscroll
        mLastScrollerY = getScrollY();
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        final int y = mScroller.getCurrY();
        final int dy = y - mLastScrollerY;
        mLastScrollerY = y;

        if (mScroller.computeScrollOffset()) {
            int scrollY = getScrollY();
            int consumedY = dy;
            int dyUnConsumed = 0;

            if (scrollY == 0) {
                // 尝试先让父控件回到初始状态
                dispatchNestedScroll(0, 0, 0, dy, mScrollOffset,
                        ViewCompat.TYPE_NON_TOUCH);
                int parentUnConsumed = dy + mScrollOffset[1];

                scrollBy(0, Math.max(0, parentUnConsumed));
            } else if (scrollY + dy < 0) {
                consumedY = -scrollY;
                dyUnConsumed = dy + scrollY;

                dispatchNestedScroll(0, consumedY, 0, dyUnConsumed, mScrollOffset,
                        ViewCompat.TYPE_NON_TOUCH);
                scrollBy(0, consumedY);
            } else {
                scrollBy(0, consumedY);
            }

            // Finally update the scroll positions and post an invalidation
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            // We can't scroll any more, so stop any indirect scrolling
            if (hasNestedScrollingParent(ViewCompat.TYPE_NON_TOUCH)) {
                stopNestedScroll(ViewCompat.TYPE_NON_TOUCH);
            }
            // and reset the scroller y
            mLastScrollerY = 0;
        }
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public boolean startNestedScroll(int axes, int type) {
        return mChildHelper.startNestedScroll(axes, type);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public void stopNestedScroll(int type) {
        mChildHelper.stopNestedScroll(type);
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        return mChildHelper.hasNestedScrollingParent(type);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow, int type) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow,
                                           int type) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    private void log(String msg) {
        Log.d(TAG, msg);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure -- ");
    }

    private Handler mHandler = new Handler();
    private int mContentHeight;
    private void getCurrentHeight() {
        int currentHeight = getContentHeight();

        if(mContentHeight != currentHeight) {
            onHeightChanged(currentHeight, mContentHeight);
            mContentHeight = currentHeight;
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getCurrentHeight();
            }
        }, 500);
    }

    protected void onHeightChanged(int height, int oldHeight) {
        Log.d(TAG, "onHeightChanged -- height = " + height);
    }
}