package com.aitangba.testproject.horizonscrollview;

import android.content.Context;
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
 * 2017-11-15 当前index为0，向右滑动，会出现瞬间白屏现象！！！
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
        int left = 0;
        int count = getChildCount();

        for(int i = 0; i < count; i ++) {
            View childView = getChildAt(i);
            int right = left + childView.getMeasuredWidth();
            int bottom = t + childView.getMeasuredHeight();
            childView.layout(left, t, right, bottom);
            if(changeLayout) {
                left = left - childView.getMeasuredWidth();
            } else {
                left = right;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
        Log.d(TAG, "onInterceptTouchEvent --- ACTION = " + getEventName(ev));
        if(mSliding) {
            requestParentDisallowInterceptTouchEvent(true);
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

    boolean changeLayout = false;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
        final int actionIndex = ev.getActionIndex();

        switch (action) {
            case MotionEvent.ACTION_MOVE:   // start a slide action
                float pointX = ev.getRawX();
                float diff = pointX - mLastPointX;
                mLastPointX = pointX;

                float scrollToX = getScrollX() + diff;

                Log.d(TAG, "onTouchEvent ---  scrollToX = " + scrollToX);
                if(scrollToX < 0 && !changeLayout) {
                    changeLayout = true;
                    requestLayout();
                } else if(scrollToX > 0 && changeLayout){
                    changeLayout = false;
                    requestLayout();
                }


                scrollBy((int) -diff, 0);//向右滑动diff > 0,
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_OUTSIDE:
                requestParentDisallowInterceptTouchEvent(false);
                mSliding = false;


                int width = getMeasuredWidth();
                int halfWidth = width / 2;

                int targetX;
                if (getScrollX() < -halfWidth) {
                    targetX = -width;
                } else if (getScrollX() >= -halfWidth && getScrollX() <= 0) {
                    targetX = 0;
                } else if (getScrollX() <= -halfWidth && getScrollX() >= 0) {
                    targetX = 0;
                } else {
                    targetX = width;
                }

                mScroller.startScroll(getScrollX(), 0, targetX - getScrollX(), 0);
                invalidate();
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
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            scrollTo(x, y);
            invalidate();
            return;
        }
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
}