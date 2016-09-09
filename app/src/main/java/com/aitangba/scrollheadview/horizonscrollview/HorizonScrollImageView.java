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
    private int mLeft; // range from -width to width (currentView.getLeft)

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
    private boolean mIsFirstMoveRight;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
        final int actionIndex = ev.getActionIndex();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onInterceptTouchEvent --- ACTION_DOWN");
                mLastPointX = ev.getRawX();
                mIsFirstMoveRight = true;
                break;
            case MotionEvent.ACTION_MOVE:

                final float curPointX = ev.getRawX();
                final float distanceX = curPointX - mLastPointX;
                mLastPointX = curPointX;
                final int tempLeft = mLeft;
                mLeft = mLeft + (int)distanceX;
                final boolean hasDiffLeft = tempLeft * mLeft < 0;
                View curView = getChildAt(mCurrentPosition);
                final int childCount = getChildCount();

                final View followView;

                int followViewOffset = hasDiffLeft ? tempLeft : (int)distanceX;
                if(mLeft < 0 ) {
                    final int nextIndex = (mCurrentPosition + 1) % childCount;
                    followView = getChildAt(nextIndex);
                } else {
                    if (mIsFirstMoveRight) {
                        mIsFirstMoveRight = false;
                        final int width = getMeasuredWidth();
                        final int height = getMeasuredHeight();
                        final int lastIndex = (mCurrentPosition - 1 + childCount) % childCount;
                        followView = getChildAt(lastIndex);
                        followView.layout(-width, 0, 0, height);
                    } else {
                        final int lastIndex = (mCurrentPosition - 1 + childCount) % childCount;
                        followView = getChildAt(lastIndex);
                    }
                }

                ViewCompat.offsetLeftAndRight(curView, (int)distanceX);
                ViewCompat.offsetLeftAndRight(followView, followViewOffset);
                Log.d(TAG, "onInterceptTouchEvent --- ACTION_MOVE" + "  mLeft = " + mLeft);
                break;
            case MotionEvent.ACTION_UP:
                mIsFirstMoveRight = false;
                break;

            default:break;
        }
//        return new ViewDragHelper().shouldInterceptTouchEvent(ev);
        return false;
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

        int curStopX;
        int followStopX;
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
        float preViewStart = 0;
        float preViewStop = curStopX;
        currentViewAnim.setFloatValues(preViewStart, preViewStop);
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
            }
        });
        animatorSet.start();
        mIsSlideAnimPlaying = true;
    }

}
