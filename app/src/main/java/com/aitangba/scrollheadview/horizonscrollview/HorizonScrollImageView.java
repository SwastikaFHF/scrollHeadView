package com.aitangba.scrollheadview.horizonscrollview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
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

    private int mCurrentPosition;

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
//        new ViewPager()
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
    }

    private float mLastPointX;  //记录手势在屏幕上的X轴坐标
    private int mLeft; // range from -width to width (currentView.getLeft)

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
        final int actionIndex = ev.getActionIndex();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onInterceptTouchEvent --- ACTION_DOWN");
                mLastPointX = ev.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                final int currentPosition = mCurrentPosition;
                final int childCount = getChildCount();
                final int width = getMeasuredWidth();
                final float curPointX = ev.getRawX();
                final float distanceX = curPointX - mLastPointX;
                mLastPointX = curPointX;

                final int tempLeft = mLeft;
                mLeft = mLeft + (int)distanceX;
                final int curLeft = mLeft;

                float nextViewX;
                float lastViewX;
                if(tempLeft <= 0 && curLeft >= 0) {  // <--
                    lastViewX = - width + curLeft;
                    nextViewX = width;
                } else if(tempLeft <= 0 && curLeft < 0) {
                    lastViewX = -width;
                    nextViewX = width + curLeft;
                } else if(tempLeft > 0 && curLeft > 0) {  // -->
                    nextViewX = -width;
                    lastViewX = distanceX;
                } else {
                    nextViewX = curLeft;
                    lastViewX = -tempLeft;
                }

                final int lastIndex = (currentPosition - 1 + childCount) % childCount;
                final View lastView = getChildAt(lastIndex);
                lastView.setX(lastViewX);

                final View curView = getChildAt(currentPosition);
                curView.setX(curLeft);

                final int nextIndex = (currentPosition + 1) % childCount;
                final View nextView = getChildAt(nextIndex);
                nextView.setX(nextViewX);

                Log.d(TAG, "onInterceptTouchEvent --- ACTION_MOVE" + " |  tempLeft = " + tempLeft + "  curLeft = " + curLeft);
                break;
            case MotionEvent.ACTION_UP:
//                startSlideAnim();
//                resetLastView(mCurrentPosition);
                break;

            default:break;
        }
        return false;
    }

    private View prepareLastView(int currentPosition) {
        final int childCount = getChildCount();
        final int width = getMeasuredWidth();
        final int lastIndex = (currentPosition - 1 + childCount) % childCount;
        final View lastView = getChildAt(lastIndex);
        final int lastViewLeft = lastView.getLeft();
        final boolean needMove = lastViewLeft >= width;
        if(needMove) {
            final int height = getMeasuredHeight();
            lastView.layout(-width, 0, 0, height);
        }
        return lastView;
    }

    private void resetLastView(int currentPosition) {
        final int childCount = getChildCount();
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        final int lastIndex = (currentPosition - 1 + childCount) % childCount;
        final View lastView = getChildAt(lastIndex);
        final int lastViewLeft = lastView.getLeft();
        final boolean isMoved = lastViewLeft <= 0;
        if(isMoved) {
            lastView.layout(width, 0, width + lastView.getMeasuredWidth(), height);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        mDragHelper.processTouchEvent(ev);
        Log.d(TAG, "onTouchEvent ===");
        return true;
    }
    private boolean mIsSlideAnimPlaying; //滑动动画展示过程中
    private void startSlideAnim() {
        final int curLeft = mLeft;
        final View curView = getChildAt(mCurrentPosition);

        final View followView;

        final int width = getMeasuredWidth();
        final int diffLeft = Math.abs(curLeft);
        final int limitWidth = width / 2;
        final int childCount = getChildCount();
        final int nextIndex = (mCurrentPosition + 1) % childCount;
        final int lastIndex = (mCurrentPosition - 1 + childCount) % childCount;

        final float curStopX;
        final float followStopX;
        final int endPositionTemp;
        if(curLeft < 0 && diffLeft >= limitWidth) {   // <--
            curStopX = -(width + curLeft);
            followView = getChildAt(nextIndex);
            followStopX = -(width + curLeft);
            endPositionTemp = nextIndex;
        } else if(curLeft < 0 && diffLeft < limitWidth) {
            curStopX = curLeft;
            followView = getChildAt(nextIndex);
            followStopX = curLeft;
            endPositionTemp = mCurrentPosition;
        } else if(curLeft > 0 && diffLeft < limitWidth) {
            curStopX = -curLeft;
            followView = getChildAt(lastIndex);
            followStopX = -curLeft;
            endPositionTemp = mCurrentPosition;
        } else {                            // -->
            curStopX = width - curLeft;
            followView = getChildAt(lastIndex);
            followStopX = width - curLeft;
            endPositionTemp = lastIndex;
        }

        final Interpolator interpolator = new DecelerateInterpolator(2f);

        ObjectAnimator currentViewAnim = new ObjectAnimator();
        currentViewAnim.setInterpolator(interpolator);
        currentViewAnim.setProperty(View.TRANSLATION_X);
        float currentViewStart = 0;
        float preViewStop = curStopX;
        currentViewAnim.setFloatValues(currentViewStart, preViewStop);
        currentViewAnim.setTarget(curView);

        ObjectAnimator followViewAnim = new ObjectAnimator();
        followViewAnim.setInterpolator(interpolator);
        followViewAnim.setProperty(View.TRANSLATION_X);
        float followViewStart = 0;
        float followViewEnd = followStopX;
        followViewAnim.setFloatValues(followViewStart, followViewEnd);
        followViewAnim.setTarget(followView);

        // play animation together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(3000);
        animatorSet.playTogether(currentViewAnim, followViewAnim);
        animatorSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentPosition = endPositionTemp;
                mIsSlideAnimPlaying = false;
                mLeft = 0;
//                resetLastView(mCurrentPosition);
                curView.setX(0);
            }
        });
        animatorSet.start();
        mIsSlideAnimPlaying = true;
    }

}
