package com.aitangba.scrollheadview.horizonscrollview;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.aitangba.scrollheadview.R;

/**
 * Created by fhf11991 on 2016/8/31.
 */
public class HorizonScrollImageView extends FrameLayout {
    private static final boolean DEBUG = false;
    private static final String TAG = "HorizonScrollImageView";

    private int mGutterSize; //两边边缘
    private int mDefaultGutterSize; //默认两边边缘
    private static final int DEFAULT_GUTTER_SIZE = 16; // dips
    private int mTouchSlop;

    private ImageView mFirstImageView;

    public HorizonScrollImageView(Context context) {
        this(context, null);
    }

    public HorizonScrollImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizonScrollImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        final float density = context.getResources().getDisplayMetrics().density;
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mDefaultGutterSize = (int) (DEFAULT_GUTTER_SIZE * density);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);


        mFirstImageView = new ImageView(context);
        mFirstImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mFirstImageView.setImageResource(R.mipmap.ic_launcher);
        addView(mFirstImageView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int measuredWidth = getMeasuredWidth();
        final int maxGutterSize = measuredWidth / 10;
        mGutterSize = Math.min(maxGutterSize, mDefaultGutterSize);
    }

    private float mLastMotionX;
    private float mLastMotionY;
    private int mActivePointerId;
    private boolean mIsBeingDragged; //是否正在拖拽中

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        Log.d(TAG, "onInterceptTouchEvent ");
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = ev.getX();
                mLastMotionY = ev.getY();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
//                mIsBeingDragged = true;
                return super.onInterceptTouchEvent(ev);
//                break;
            case MotionEvent.ACTION_MOVE:
                final int activePointerId = mActivePointerId;
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float dx = x - mLastMotionX;
                final float xDiff = Math.abs(dx);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float yDiff = Math.abs(y - mLastMotionY);

                if (dx != 0 && !isGutterDrag(mLastMotionX, dx)) {
                    mLastMotionX = x;
                    mLastMotionY = y;
                    return false;
                }

                if (xDiff > mTouchSlop && xDiff * 0.5f > yDiff) {
                    mIsBeingDragged = true;
                }
                Log.d(TAG, "onInterceptTouchEvent = ACTION_MOVE");
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                return false;
        }
        return mIsBeingDragged;
    }

    /**
     * 是否属于边缘滑动
     * @param x
     * @param dx
     * @return
     */
    private boolean isGutterDrag(float x, float dx) {
        return (x < mGutterSize && dx > 0) || (x > getWidth() - mGutterSize && dx < 0);
    }

    private boolean mIsUnableToDrag;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;

        if(mIsUnableToDrag)return false;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent -- ACTION_DOWN");
                mLastMotionX = ev.getX();
                mLastMotionY = ev.getY();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            case MotionEvent.ACTION_MOVE:
                final int activePointerId = mActivePointerId;
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float dx = x - mLastMotionX;
                final float xDiff = Math.abs(dx);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float yDiff = Math.abs(y - mLastMotionY);

                Log.d(TAG, "mLastMotionY = " + mLastMotionY);

                if (dx == 0 || isGutterDrag(mLastMotionX, dx)) {
                    mIsUnableToDrag = true;
                    return false;
                }
                mLastMotionX = x;
                mLastMotionY = y;

                if (xDiff > mTouchSlop && xDiff > yDiff) {
                    mIsBeingDragged = true;
                    mIsUnableToDrag = false;
                    final float currentX = mFirstImageView.getX();
                    float targetX = currentX + dx;
                    mFirstImageView.setX(targetX);
                    mLastMotionX = x;
                    mLastMotionY = y;
                    Log.d(TAG, "currentX = " + currentX + "   dx = " + dx);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                return false;
        }
        return true;
    }
}
