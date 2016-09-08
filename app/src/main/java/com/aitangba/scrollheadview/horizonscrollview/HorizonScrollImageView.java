package com.aitangba.scrollheadview.horizonscrollview;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    private View mFirstChildView;
    private View mSecondChildView;
    private View mThirdChildView;

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

    private ViewDragHelper mDragHelper;
    private int mCurrentPosition;
    private int mLeft;

    private void init(Context context) {

        final float density = context.getResources().getDisplayMetrics().density;
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mDefaultGutterSize = (int) (DEFAULT_GUTTER_SIZE * density);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);

        mFirstChildView = createChildView();
        mSecondChildView = createChildView();
        mThirdChildView = createChildView();

        addView(mFirstChildView);
        addView(mSecondChildView);
        addView(mThirdChildView);

        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragCallback());
    }

    private View createChildView() {
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setClickable(false);
        imageView.setImageResource(R.drawable.bg_red);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "点击事件-----");
            }
        });
        return imageView;
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
            child.measure(widthSpec, heightSpec);
        }
        setMeasuredDimension(widthSpec, heightSpec);

        final int maxGutterSize = width / 10;
        mGutterSize = Math.min(maxGutterSize, mDefaultGutterSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        final int curLeft = mLeft;
        final int width = getMeasuredWidth();
        final int lastIndex = (mCurrentPosition - 1 + childCount) % childCount;
        final int nextIndex = (mCurrentPosition + 1) % childCount;

        for(int i = 0 ;i < childCount; i ++) {
            final View child = getChildAt(i);
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();

            final int left;
            if(i == lastIndex && curLeft > 0) {
                left = - (width - curLeft);
            } else if(i == mCurrentPosition) {
                left = curLeft;
                Log.d(TAG, "onLayout---" + "     left = " + left +", mLeft = " + mLeft);
            } else if(i == nextIndex && curLeft < 0) {
                left = width + curLeft;
            } else {
                left = width;
            }

            final int top = t;
            final int right = left + childWidth;
            final int bottom = top + childHeight;
            child.layout(left, top, right, bottom);
        }
        Log.d(TAG, "onLayout---");
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mDragHelper.processTouchEvent(ev);
        return true;
    }

    class ViewDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth();
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if(state == ViewDragHelper.STATE_IDLE) {
                final int childCount = getChildCount();
                final int width = getMeasuredWidth();
                final int left = mLeft;
                final int diffLeft = Math.abs(left);
                final int limitWidth = width / 2;
                if(left < 0 && diffLeft >= limitWidth) {   // <--
                    final int nextIndex = (mCurrentPosition + 1) % childCount;
                    mCurrentPosition = nextIndex;
                } else if (left > 0 && diffLeft >= limitWidth) {             // -->
                    final int lastIndex = (mCurrentPosition - 1 + childCount) % childCount;
                    mCurrentPosition = lastIndex;
                }
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            mLeft = left;
            Log.d(TAG, "changedView.getLeft() = " + changedView.getLeft() + "   ; left = " + left);
            requestLayout();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            final int width = getMeasuredWidth();
            final int left = mLeft;
            final int diffLeft = Math.abs(left);
            final int limitWidth = width / 2;

            final int finalLeft;
            if(left < 0 && diffLeft >= limitWidth) {   // <--
                finalLeft = -width;
            } else if(left > 0 && diffLeft >= limitWidth)  {   // -->
                finalLeft = width;
            } else {
                finalLeft = 0;
            }
            mDragHelper.settleCapturedViewAt(finalLeft, 0);
            invalidate();
        }
    }
}
