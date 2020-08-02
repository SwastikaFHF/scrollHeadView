package com.aitangba.testproject.view.nestwebview;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild2;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.webkit.WebView;
import android.widget.OverScroller;

/**
 * Created by XBeats on 2020/7/30
 */
public class NestWebView extends WebView implements NestedScrollingChild2 {

    private static final String TAG = "NestWebView_TAG";


    private final int[] mScrollConsumed = new int[2];
    private final int[] mScrollOffset = new int[2];

    private int mLastMotionY;

    private VelocityTracker mVelocityTracker;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private OverScroller mScroller;
    private int mLastScrollerY;


    private final NestedScrollingChildHelper mChildHelper;

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

    public void fling(int velocityY) {
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
        Log.d(TAG, String.format("computeScroll getScrollY = %d", getScrollY()));
        if (mScroller.computeScrollOffset()) {
            final int x = mScroller.getCurrX();
            final int y = mScroller.getCurrY();
            int dy = y - mLastScrollerY;
            if (dy != 0) {
                int scrollY = getScrollY();
                int dyUnConsumed = 0;
                int consumedY = dy;
                if (scrollY == 0) {
                    dyUnConsumed = dy;
                    consumedY = 0;
                } else if (scrollY + dy < 0) {
                    dyUnConsumed = dy + scrollY;
                    consumedY = -scrollY;
                }

                if (!dispatchNestedScroll(0, consumedY, 0, dyUnConsumed, null,
                        ViewCompat.TYPE_NON_TOUCH)) {

                }
            }

            // Finally update the scroll positions and post an invalidation
            mLastScrollerY = y;
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

    private int mScrollOffsetRecord;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initVelocityTrackerIfNotExists();
        Log.d("Custom_TAG", "onTouchEvent -- " + actionToString(event.getAction()));
        MotionEvent motionEvent = MotionEvent.obtain(event);

        final int actionMasked = event.getAction();

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
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
                mScrollOffsetRecord = 0;
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int initialVelocity = (int) velocityTracker.getYVelocity();
                if (Math.abs(initialVelocity) > mMinimumVelocity) {
                    fling(-initialVelocity);
                }
            case MotionEvent.ACTION_CANCEL:
                stopNestedScroll();
                recycleVelocityTracker();
                break;
            case MotionEvent.ACTION_MOVE:

                final int y = (int) event.getY() + mScrollOffsetRecord;
                final int deltaY =  y - mLastMotionY;

                Log.d(TAG, String.format("move: y = %d, deltaY = %d, mScrollOffsetRecord =  %d", y , deltaY, mScrollOffsetRecord));
                mLastMotionY = y;
                int scrollY = getScrollY();


                // 预处理滑动事件
                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
//                    Log.d(TAG, String.format("onPreScroll: mScrollConsumed[1] = %d, mScrollOffset[1] = %d: ", mScrollConsumed[1] , mScrollOffset[1]));
                    motionEvent.offsetLocation(0, mScrollConsumed[1]);
                    mScrollOffsetRecord += mScrollOffset[1];
                }

                // 向上滑动，优先查看Parent要不要处理，
                // 向下滑动，优先查看自己要不要处理
                int maxScrollY = (int) (getContentHeight() * getScale() - getHeight());

                if(scrollY == 0) {
                    dispatchNestedScroll(0, 0, 0, -deltaY, mScrollOffset);
                    mScrollOffsetRecord += mScrollOffset[1];

                    Log.d(TAG, String.format("scrollY = 0: mScrollOffset[1] = %d, dy = %d ", mScrollOffset[1], deltaY));
                    if(deltaY != 0 && mScrollOffset[1] == 0) {
                        motionEvent.offsetLocation(0, mScrollOffset[1]);
                        return super.onTouchEvent(event);
                    }
                    return true;
                } else if(scrollY < maxScrollY) {
                    Log.d(TAG, String.format("scrollY > 0: mScrollOffset[1] = %d: ", mScrollOffset[1]));
                    return super.onTouchEvent(event);
                } else {
                    Log.d(TAG, String.format("scrollY = max: mScrollOffset[1] = %d: ", mScrollOffset[1]));
                    if(deltaY > 0) {
                        return super.onTouchEvent(event);
                    } else {
                        dispatchNestedScroll(0, 0, 0, -deltaY, mScrollOffset);
                        mScrollOffsetRecord += mScrollOffset[1];
                        return true;
                    }
                }
            default:
                break;
        }
        return super.onTouchEvent(motionEvent);
    }

    // NestedScrollingChild

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

    public static String actionToString(int action) {
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
        }
        int index = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                return "ACTION_POINTER_DOWN(" + index + ")";
            case MotionEvent.ACTION_POINTER_UP:
                return "ACTION_POINTER_UP(" + index + ")";
            default:
                return Integer.toString(action);
        }
    }


}