package com.aitangba.testproject.horizonscrollview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Scroller;

/**
 * Created by fhf11991 on 2016/8/31.
 */
public class SlideViewGroup extends ViewGroup {
    private static final String TAG = "SlideViewGroup";

    private int mTouchSlop;
    private Scroller mScroller;

    public SlideViewGroup(Context context) {
        this(context, null);
    }

    public SlideViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();

        mScroller = new Scroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int childCount = getChildCount();
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        final int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        final int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.measure(widthSpec, heightSpec);
        }
        setMeasuredDimension(widthSpec, heightSpec);
    }

    private int mCurrentViewLeft = 0;
    private int mCurrentPosition = 0;
    private float mLastPointX = 0;  //记录手势在屏幕上的X轴坐标
    private boolean mSliding = false;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        final int childCount = getChildCount();

        // current child view
        View currentChildView = getChildAt(mCurrentPosition);
        final int left = mCurrentViewLeft;
        final int right = mCurrentViewLeft + currentChildView.getMeasuredWidth();
        currentChildView.layout(left, t, right, t + currentChildView.getMeasuredHeight());

        if (childCount <= 1) {
            return;
        }

        // other child view
        int otherChildViewPosition;
        View otherChildView;
        int otherViewLeft;
        if (mCurrentViewLeft < 0) { // turn right
            otherChildViewPosition = (mCurrentPosition + 1) % childCount;
            otherChildView = getChildAt(otherChildViewPosition);
            otherViewLeft = right;
        } else if (mCurrentViewLeft > 0) { // turn left
            otherChildViewPosition = (mCurrentPosition - 1 + childCount) % childCount;
            otherChildView = getChildAt(otherChildViewPosition);
            otherViewLeft = left - otherChildView.getMeasuredWidth();
        } else {
            return;
        }
        otherChildView.layout(otherViewLeft, t, otherViewLeft + otherChildView.getMeasuredWidth(), t + currentChildView.getMeasuredHeight());
    }

    private String getEventName(MotionEvent ev) {

        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                return "ACTION_DOWN";
            case MotionEvent.ACTION_MOVE:
                return "ACTION_MOVE";
            case MotionEvent.ACTION_UP:
                return "ACTION_UP";
            case MotionEvent.ACTION_OUTSIDE:
                return "ACTION_OUTSIDE";
            case MotionEvent.ACTION_CANCEL:
                return "ACTION_CANCEL";

            default:
                break;
        }
        return "unknown_action";
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
        Log.d(TAG, "onInterceptTouchEvent --- ACTION = " + getEventName(ev));
        if(mSliding) {
            return true;
        }

        if(action == MotionEvent.ACTION_DOWN) { // just record origin position of the action
            mLastPointX = ev.getRawX();
        } else if(action == MotionEvent.ACTION_MOVE) {
            float pointX = ev.getRawX();
            float diff = pointX - mLastPointX;
            if(Math.abs(diff) > mTouchSlop) { // just a slide action
                requestParentDisallowInterceptTouchEvent(true);
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
        final int actionIndex = ev.getActionIndex();

        switch (action) {
            case MotionEvent.ACTION_MOVE:   // start a slide action
                float pointX = ev.getRawX();
                float diff = pointX - mLastPointX;
                mLastPointX = pointX;
                mCurrentViewLeft += diff;
                requestLayout();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_OUTSIDE:
                requestParentDisallowInterceptTouchEvent(false);
                mSliding = false;

                int width = getMeasuredWidth();
                int halfWidth = width / 2;

                int targetX;
                if (mCurrentViewLeft < -halfWidth) {
                    targetX = -width;
                } else if (mCurrentViewLeft >= -halfWidth && mCurrentViewLeft <= 0) {
                    targetX = 0;
                } else if (mCurrentViewLeft <= -halfWidth && mCurrentViewLeft >= 0) {
                    targetX = 0;
                } else {
                    targetX = width;
                }
//                mScroller.startScroll(mCurrentViewLeft, 0, targetX - mCurrentViewLeft, 0);
//                Log.d(TAG, "ACTION_OUTSIDE ---> mCurrentViewLeft = " + mCurrentViewLeft + "  dx = " + (targetX - mCurrentViewLeft));
                break;

            default:
                break;
        }
        return true;
    }

    private void requestParentDisallowInterceptTouchEvent(boolean disallowIntercept) {
        final ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mCurrentViewLeft = mScroller.getCurrX();
            Log.d(TAG, "computeScroll ---> getCurrX = " + mCurrentViewLeft);
            requestLayout();
        } else {
            Log.d(TAG, "computeScroll ---> over " + mCurrentViewLeft);
        }
    }
}
