package com.aitangba.scrollheadview.horizonscrollview;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.aitangba.scrollheadview.R;

/**
 * Created by fhf11991 on 2016/8/31.
 */
public class HorizonScrollImageView extends ViewGroup {
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
//        mFirstImageView.setScaleType(ImageView.ScaleType.FIT_XY);
//        mFirstImageView.setImageResource(R.mipmap.ic_launcher);
        mFirstImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "点击事件-----");
            }
        });
        addView(mFirstImageView);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int childCount = getChildCount();
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        final int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        final int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        for(int i = 0 ;i < childCount; i ++) {
            View child = getChildAt(i);
            if(child == mFirstImageView) {
                child.measure(widthSpec, heightSpec);
            }
        }
        setMeasuredDimension(widthSpec, heightSpec);

        final int maxGutterSize = width / 10;
        mGutterSize = Math.min(maxGutterSize, mDefaultGutterSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();

        for(int i = 0 ;i < childCount; i ++) {
            View child = getChildAt(i);
            if(child == mFirstImageView) {
                child.layout(l, t, r, b);
            }
        }

    }

    private void logActionName(int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onInterceptTouchEvent =" + "ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onInterceptTouchEvent =" + "ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onInterceptTouchEvent =" + "ACTION_UP");
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "onInterceptTouchEvent =" + "ACTION_CANCEL");
                break;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        logActionName(action);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onInterceptTouchEvent -- ACTION_DOWN");
                mLastMotionX = ev.getX();
                mLastMotionY = ev.getY();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                return false;
            case MotionEvent.ACTION_MOVE:
                final int activePointerId = mActivePointerId;
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float dx = x - mLastMotionX;
                final float xDiff = Math.abs(dx);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float yDiff = Math.abs(y - mLastMotionY);

                if (dx != 0 &&  !isGutterDrag(mLastMotionX, dx)) {
                    mIsUnableToDrag = true;
                    return false;
                }

                if (xDiff > mTouchSlop && xDiff * 0.5f > yDiff) {
                    mIsBeingDragged = true;
                } else if (yDiff > mTouchSlop) {
                    mIsUnableToDrag = true;
                }
                break;
            default:
                break;
        }
        Log.d(TAG, "mIsBeingDragged = " + mIsBeingDragged);
        return mIsBeingDragged;
    }

    private float mLastMotionX;
    private float mLastMotionY;
    private int mActivePointerId;
    private boolean mIsBeingDragged; //是否正在拖拽中

    private boolean mIsUnableToDrag;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onTouchEvent --");
        if(mIsUnableToDrag)return false;

        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;

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

                if (xDiff > mTouchSlop && xDiff >= yDiff) {  //水平滑动
                    mIsBeingDragged = true;
                    mIsUnableToDrag = false;
                    final float currentX = mFirstImageView.getX();
                    float targetX = currentX + dx;
                    ViewCompat.offsetLeftAndRight(mFirstImageView, (int)targetX);
                    mLastMotionX = x;
                    mLastMotionY = y;
                    Log.d(TAG, "currentX = " + currentX + "   dx = " + dx);
                } else if (xDiff > mTouchSlop && xDiff < yDiff){ //竖直滑动
                    mIsUnableToDrag = true;
                    return false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                return false;
        }
        return true;
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
}
