package com.aitangba.testproject.view.drag;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.util.IntProperty;
import android.util.Log;
import android.util.Property;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by fhf11991 on 2018/8/9
 */
public class ScrollChildView extends LinearLayout implements NestedScrollingChild {

    private static final String TAG = "ScrollChildView";
    private static final float HEAD_VIEW_HEIGHT = 44F; // dp
    private static final float IMAGE_RADIUS = 40F; // dp
    private static final float MIDDLE_SPACE_HEIGHT = 100F; // dp
    private static final float HIDE_SPACE_HEIGHT = 100F; // dp

    private NestedScrollingChildHelper mScrollingChildHelper;
    private VelocityTracker mVelocityTracker = VelocityTracker.obtain();

    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;

    private final int mMaxTopMargin;
    private final int mHeadViewHeight;
    private final int mImageRadius;
    private final int mMiddleSpaceHeight;

    public ScrollChildView(Context context) {
        this(context, null);
    }

    public ScrollChildView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollChildView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

        mImageRadius = (int) dp2px(context, IMAGE_RADIUS);
        mHeadViewHeight = (int) dp2px(context, HEAD_VIEW_HEIGHT);
        mMiddleSpaceHeight = (int) dp2px(context, HEAD_VIEW_HEIGHT + MIDDLE_SPACE_HEIGHT);
        mMaxTopMargin = (int) dp2px(context, HEAD_VIEW_HEIGHT + MIDDLE_SPACE_HEIGHT + HIDE_SPACE_HEIGHT);
    }

    private ImageView mImageView;
    private ObserverSizeTextView mTextView;

    public void bindImageView(ImageView imageView) {
        mImageView = imageView;
    }

    public void bindTextView(ObserverSizeTextView textView) {
        mTextView = textView;
        mTextView.setOnSizeChangedListener(new ObserverSizeTextView.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(ObserverSizeTextView textView, int w, int h, int oldw, int oldh) {
                MarginLayoutParams params = (MarginLayoutParams) textView.getLayoutParams();
                params.topMargin = mHeadViewHeight + mImageRadius - h / 2;
            }
        });
    }

    private static float dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5F;
    }

    private float mLastMotionY;
    private boolean mIsBeingDragged;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final int actionMasked = event.getActionMasked();

        final int action = event.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && (mIsBeingDragged)) {
            return true;
        }

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = event.getRawY();
                MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
                mIsBeingDragged = params.topMargin == 0;
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                mVelocityTracker = VelocityTracker.obtain();
                break;

            case MotionEvent.ACTION_MOVE:
                final float y = event.getRawY();

                final float yDiff = Math.abs(y - mLastMotionY);
                if (yDiff > mTouchSlop) {
                    mIsBeingDragged = true;
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                /* Release the drag */
                mIsBeingDragged = false;
                stopNestedScroll();

                final VelocityTracker velocityTracker = mVelocityTracker;
                Log.d(TAG, "onInterceptTouchEvent --- " + velocityTracker.getYVelocity());
                velocityTracker.recycle();
                break;
        }

        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int actionMasked = event.getActionMasked();
        MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = event.getRawY();
                mIsBeingDragged = params.topMargin == 0;
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                break;

            case MotionEvent.ACTION_MOVE:
                View view = (View) getParent();
                final float y = event.getRawY();

                int dy = (int) (y - mLastMotionY);
                mLastMotionY = y;

                Log.d(TAG, "onTouchEvent --- "
                        + " dy = " + dy
                        + " topMargin = " + params.topMargin
                        + " ScrollY = " + view.getScrollY());
                int minTopMargin = mHeadViewHeight;
                if (params.topMargin == minTopMargin) {
                    if (dy < 0) {
                        int dyConsumed = 0;
                        int dyUnConsumed = dy - dyConsumed;

                        dispatchDragEvent(params, dyConsumed, dyUnConsumed);
                    } else {
                        if (view.getScrollY() != 0) {
                            int dyConsumed = 0;
                            int dyUnConsumed = dy - dyConsumed;

                            dispatchDragEvent(params, dyConsumed, dyUnConsumed);
                            break;
                        } else {
                            int dyConsumed = dy;
                            int dyUnConsumed = dy - dyConsumed;

                            dispatchDragEvent(params, dyConsumed, dyUnConsumed);
                        }
                    }
                } else if (params.topMargin == mMaxTopMargin) {
                    if (dy < 0) {
                        int dyConsumed = dy;
                        int dyUnConsumed = dy - dyConsumed;

                        dispatchDragEvent(params, dyConsumed, dyUnConsumed);
                    } else {
                        int dyConsumed = 0;
                        int dyUnConsumed = dy - dyConsumed;

                        dispatchDragEvent(params, dyConsumed, dyUnConsumed);
                    }
                } else {
                    if (params.topMargin + dy < minTopMargin) {
                        int dyConsumed = minTopMargin - params.topMargin;
                        int dyUnConsumed = dy - dyConsumed;

                        dispatchDragEvent(params, dyConsumed, dyUnConsumed);
                        break;
                    } else if (params.topMargin + dy > mMaxTopMargin) {
                        int dyConsumed = mMaxTopMargin - params.topMargin;
                        int dyUnConsumed = dy - dyConsumed;

                        dispatchDragEvent(params, dyConsumed, dyUnConsumed);
                    } else {
                        int dyConsumed = dy;
                        int dyUnConsumed = dy - dyConsumed;

                        dispatchDragEvent(params, dyConsumed, dyUnConsumed);
                    }

                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.addMovement(event);
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                /* Release the drag */
                mIsBeingDragged = false;
                stopNestedScroll();
                recoveryViews();

                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                final int initialVelocity = (int) velocityTracker.getYVelocity();
                Log.d(TAG, "onTouchEvent --- " + velocityTracker.getYVelocity());
                velocityTracker.clear();
                velocityTracker.recycle();
                if (params.topMargin == 0 && (Math.abs(initialVelocity) > mMinimumVelocity)) {
                    int velocityY = -initialVelocity;
                    startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                    dispatchNestedFling(0, velocityY, false);
                }
                break;
        }

        mVelocityTracker.addMovement(event);
        return true;
    }

    private void dispatchDragEvent(MarginLayoutParams params, int dyConsumed, int dyUnConsumed) {
        params.topMargin = params.topMargin + dyConsumed;
        requestLayout();

        dispatchNestedScroll(0, -dyConsumed, 0, -dyUnConsumed, null);
        refreshViews(-dyConsumed, params.topMargin);
    }

    private void refreshViews(int dy, int topMargin) {
        int maxTranslationY = mMiddleSpaceHeight - (mImageRadius + mHeadViewHeight / 2);
        int middleMargin = mMiddleSpaceHeight;
        if(topMargin <= maxTranslationY) {
            final float scaleFactor = 0.4F;
            final float maxImageTranslationY = mHeadViewHeight / 2 + mImageRadius;
            final float maxImageTranslationX = (1 - scaleFactor) * mImageRadius;
            mImageView.setTranslationY(-maxImageTranslationY);
            mImageView.setTranslationX(-maxImageTranslationX);
            mImageView.setScaleX(scaleFactor);
            mImageView.setScaleY(scaleFactor);

            final float textScaleFactor = 0.8F;
            final float maxTextTranslationY = mHeadViewHeight / 2 + mImageRadius;
            final int width = mTextView.getMeasuredWidth();
            MarginLayoutParams textLayoutParams = (MarginLayoutParams) mTextView.getLayoutParams();
            final float maxTextTranslationX = getMeasuredWidth() / 2 - textLayoutParams.leftMargin - width / 2 - (1 - scaleFactor) / 2 * width;
            mTextView.setTranslationY(-maxTextTranslationY);
            mTextView.setTranslationX(maxTextTranslationX);
            mTextView.setScaleX(textScaleFactor);
            mTextView.setScaleY(textScaleFactor);
        } else if(topMargin > maxTranslationY && topMargin < middleMargin) {
            refreshImageView(dy);
            refreshTextView(dy);
        } else {
            mImageView.setTranslationY(0);
            mImageView.setTranslationX(0);
            mImageView.setScaleX(1);
            mImageView.setScaleY(1);

            mTextView.setTranslationY(0);
            mTextView.setTranslationX(0);
            mTextView.setScaleX(1);
            mTextView.setScaleY(1);
        }
    }

    private void refreshImageView(int dyConsumed) {
        final float scaleFactor = 0.4F;
        final float maxImageTranslationY = mHeadViewHeight / 2 + mImageRadius;
        final float maxImageTranslationX = (1 - scaleFactor) * mImageRadius;
        if (dyConsumed > 0) {
            float translationY = Math.abs(mImageView.getTranslationY()) + Math.abs(dyConsumed);
            if (translationY > maxImageTranslationY) {
                translationY = maxImageTranslationY;
            }
            mImageView.setTranslationY(-translationY);
            mImageView.setTranslationX(-maxImageTranslationX * translationY / maxImageTranslationY);
            float scale = 1 - (1 - scaleFactor) * translationY / maxImageTranslationY;
            mImageView.setScaleX(scale);
            mImageView.setScaleY(scale);
        } else if (dyConsumed < 0) {
            int minTranslationY = 0;
            float translationY = Math.abs(mImageView.getTranslationY()) - Math.abs(dyConsumed);
            if (translationY < minTranslationY) {
                translationY = minTranslationY;
            }
            mImageView.setTranslationY(-translationY);
            mImageView.setTranslationX(-maxImageTranslationX * translationY / maxImageTranslationY);
            float scale = 1 - (1 - scaleFactor) * translationY / maxImageTranslationY;
            mImageView.setScaleX(scale);
            mImageView.setScaleY(scale);
        }
    }

    private void refreshTextView(int dyConsumed) {
        final float scaleFactor = 0.8F;
        final float maxTextTranslationY = mHeadViewHeight / 2 + mImageRadius;
        final int width = mTextView.getMeasuredWidth();
        MarginLayoutParams textLayoutParams = (MarginLayoutParams) mTextView.getLayoutParams();
        final float maxTextTranslationX = getMeasuredWidth() / 2 - textLayoutParams.leftMargin - width / 2 - (1 - scaleFactor) / 2 * width;

        if (dyConsumed > 0) {
            float translationY = Math.abs(mTextView.getTranslationY()) + Math.abs(dyConsumed);
            if (translationY > maxTextTranslationY) {
                translationY = maxTextTranslationY;
            }
            mTextView.setTranslationY(-translationY);
            mTextView.setTranslationX(maxTextTranslationX * translationY / maxTextTranslationY);
            float scale = 1 - (1 - scaleFactor) * translationY / maxTextTranslationY;
            mTextView.setScaleX(scale);
            mTextView.setScaleY(scale);
        } else if (dyConsumed < 0) {
            int minTranslationY = 0;
            float translationY = Math.abs(mTextView.getTranslationY()) - Math.abs(dyConsumed);
            if (translationY < minTranslationY) {
                translationY = minTranslationY;
            }
            mTextView.setTranslationY(-translationY);
            mTextView.setTranslationX(maxTextTranslationX * translationY / maxTextTranslationY);
            float scale = 1 - (1 - scaleFactor) * translationY / maxTextTranslationY;
            mTextView.setScaleX(scale);
            mTextView.setScaleY(scale);
        }
    }

    private void recoveryViews() {
        final int limitMargin = mHeadViewHeight + mImageRadius;
        final int minMarginTop = mHeadViewHeight;

        View view = this;
        MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
        final int topMargin = params.topMargin;
        Log.d(TAG, "recoveryViews ------ "
                + " params.topMargin = " + topMargin
                + " limitMargin = " + limitMargin
                + " mMiddleTopMargin = " + mMiddleSpaceHeight);
        if (topMargin <= limitMargin) {
            ObjectAnimator marginTopAnim = ObjectAnimator.ofInt(view, MARGIN_TOP, topMargin, minMarginTop);

            final float imageScaleFactor = 0.4F;
            final float maxImageTranslationY = mHeadViewHeight / 2 + mImageRadius;
            final float maxImageTranslationX = (1 - imageScaleFactor) * mImageRadius;

            ObjectAnimator imageTranXAnim = ObjectAnimator.ofFloat(mImageView, TRANSLATION_X, mImageView.getTranslationX(), -maxImageTranslationX);
            ObjectAnimator imageTranYAnim = ObjectAnimator.ofFloat(mImageView, TRANSLATION_Y, mImageView.getTranslationY(), -maxImageTranslationY);
            ObjectAnimator imageScaleXAnim = ObjectAnimator.ofFloat(mImageView, SCALE_X, mImageView.getScaleX(), imageScaleFactor);
            ObjectAnimator imageScaleYAnim = ObjectAnimator.ofFloat(mImageView, SCALE_Y, mImageView.getScaleY(), imageScaleFactor);

            final float textScaleFactor = 0.8F;
            final int width = mTextView.getMeasuredWidth();
            MarginLayoutParams textLayoutParams = (MarginLayoutParams) mTextView.getLayoutParams();
            final float maxTextTranslationX = getMeasuredWidth() / 2 - textLayoutParams.leftMargin - width / 2 - (1 - textScaleFactor) / 2 * width;

            ObjectAnimator textTranXAnim = ObjectAnimator.ofFloat(mTextView, TRANSLATION_X, mTextView.getTranslationX(), maxTextTranslationX);
            ObjectAnimator textTranYAnim = ObjectAnimator.ofFloat(mTextView, TRANSLATION_Y, mTextView.getTranslationY(), -maxImageTranslationY);
            ObjectAnimator textScaleXAnim = ObjectAnimator.ofFloat(mTextView, SCALE_X, mTextView.getScaleX(), textScaleFactor);
            ObjectAnimator textScaleYAnim = ObjectAnimator.ofFloat(mTextView, SCALE_Y, mTextView.getScaleY(), textScaleFactor);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(200);
            animatorSet.setInterpolator(new DecelerateInterpolator(2f));
            animatorSet.playTogether(marginTopAnim,
                    imageTranXAnim, imageTranYAnim, imageScaleXAnim, imageScaleYAnim,
                    textTranXAnim, textTranYAnim, textScaleXAnim, textScaleYAnim);
            animatorSet.start();

        } else {
            ObjectAnimator imageTranXAnim = ObjectAnimator.ofFloat(mImageView, TRANSLATION_X, mImageView.getTranslationX(), 0);
            ObjectAnimator imageTranYAnim = ObjectAnimator.ofFloat(mImageView, TRANSLATION_Y, mImageView.getTranslationY(), 0);
            ObjectAnimator imageScaleXAnim = ObjectAnimator.ofFloat(mImageView, SCALE_X, mImageView.getScaleX(), 1);
            ObjectAnimator imageScaleYAnim = ObjectAnimator.ofFloat(mImageView, SCALE_Y, mImageView.getScaleY(), 1);

            ObjectAnimator textTranXAnim = ObjectAnimator.ofFloat(mTextView, TRANSLATION_X, mTextView.getTranslationX(), 0);
            ObjectAnimator textTranYAnim = ObjectAnimator.ofFloat(mTextView, TRANSLATION_Y, mTextView.getTranslationY(), 0);
            ObjectAnimator textScaleXAnim = ObjectAnimator.ofFloat(mTextView, SCALE_X, mTextView.getScaleX(), 1);
            ObjectAnimator textScaleYAnim = ObjectAnimator.ofFloat(mTextView, SCALE_Y, mTextView.getScaleY(), 1);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(200);
            animatorSet.setInterpolator(new DecelerateInterpolator(2f));

            if (topMargin - mMiddleSpaceHeight > (mMaxTopMargin - mMiddleSpaceHeight) * 0.4) {
                SpringAnimation anim = new SpringAnimation(view, SPRING_MARGIN_TOP, mMiddleSpaceHeight);
                anim.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
                anim.setStartValue(topMargin);
                anim.start();

                animatorSet.playTogether(
                        imageTranXAnim, imageTranYAnim, imageScaleXAnim, imageScaleYAnim,
                        textTranXAnim, textTranYAnim, textScaleXAnim, textScaleYAnim);
                animatorSet.start();
            } else {
                ObjectAnimator marginTopAnim = ObjectAnimator.ofInt(view, MARGIN_TOP, topMargin, mMiddleSpaceHeight);
                animatorSet.playTogether(marginTopAnim,
                        imageTranXAnim, imageTranYAnim, imageScaleXAnim, imageScaleYAnim,
                        textTranXAnim, textTranYAnim, textScaleXAnim, textScaleYAnim);
                animatorSet.start();
            }
        }
    }

    /**
     * 设置是否允许嵌套滑动
     *
     * @param enabled
     */
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    /**
     * 是否允许嵌套滑动
     *
     * @return
     */
    @Override
    public boolean isNestedScrollingEnabled() {
        return mScrollingChildHelper.isNestedScrollingEnabled();
    }

    /**
     * 告诉开始嵌套滑动流程，调用这个函数的时候会去找嵌套滑动的父控件。如果找到了父控件并且父控件说可以滑动就返回true，否则返回false
     * (一般ACTION_DOWN里面调用)
     *
     * @param axes:支持嵌套滚动轴。水平方向，垂直方向，或者不指定
     * @return true 父控件说可以滑动，false 父控件说不可以滑动
     */
    @Override
    public boolean startNestedScroll(int axes) {
        return mScrollingChildHelper.startNestedScroll(axes);
    }

    /**
     * 停止嵌套滑动流程(一般ACTION_UP里面调用)
     */
    @Override
    public void stopNestedScroll() {
        mScrollingChildHelper.stopNestedScroll();
    }

    /**
     * 是否有嵌套滑动对应的父控件
     *
     * @return
     */
    @Override
    public boolean hasNestedScrollingParent() {
        return mScrollingChildHelper.hasNestedScrollingParent();
    }

    /**
     * 在嵌套滑动的子View滑动之前，告诉父View滑动的距离，让父View做相应的处理。
     *
     * @param dx             告诉父View水平方向需要滑动的距离
     * @param dy             告诉父View垂直方向需要滑动的距离
     * @param consumed       出参. 如果不是null, 则告诉子View父View滑动的情况， consumed[0]父View告诉子View水平方向滑动的距离(dx)
     *                       consumed[1]父View告诉子View垂直方向滑动的距离(dy).
     * @param offsetInWindow 可选 length=2的数组，如果父View滑动导致子View的窗口发生了变化（子View的位置发生了变化）
     *                       该参数返回x(offsetInWindow[0]) y(offsetInWindow[1])方向的变化
     *                       如果你记录了手指最后的位置，需要根据参数offsetInWindow计算偏移量，才能保证下一次的touch事件的计算是正确的。
     * @return true 父View滑动了，false 父View没有滑动。
     */
    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    /**
     * 在嵌套滑动的子View滑动之后再调用该函数向父View汇报滑动情况。
     *
     * @param dxConsumed     子View水平方向滑动的距离
     * @param dyConsumed     子View垂直方向滑动的距离
     * @param dxUnconsumed   子View水平方向没有滑动的距离
     * @param dyUnconsumed   子View垂直方向没有滑动的距离
     * @param offsetInWindow 出参 如果父View滑动导致子View的窗口发生了变化（子View的位置发生了变化）
     *                       该参数返回x(offsetInWindow[0]) y(offsetInWindow[1])方向的变化
     *                       如果你记录了手指最后的位置，需要根据参数offsetInWindow计算偏移量，才能保证下一次的touch事件的计算是正确的。
     * @return true 如果父View有滑动做了相应的处理, false 父View没有滑动.
     */
    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    /**
     * 在嵌套滑动的子View fling之前告诉父View fling的情况。
     *
     * @param velocityX 水平方向的速度
     * @param velocityY 垂直方向的速度
     * @return 如果父View fling了
     */
    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    /**
     * 在嵌套滑动的子View fling之后再调用该函数向父View汇报fling情况。
     *
     * @param velocityX 水平方向的速度
     * @param velocityY 垂直方向的速度
     * @param consumed  true 如果子View fling了, false 如果子View没有fling
     * @return true 如果父View fling了
     */
    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mScrollingChildHelper.onDetachedFromWindow();
    }

    private static final Property<View, Integer> MARGIN_TOP = new IntProperty<View>("margin_top") {

        @Override
        public Integer get(View object) {
            MarginLayoutParams params = (MarginLayoutParams) object.getLayoutParams();
            return params.topMargin;
        }

        @Override
        public void setValue(View object, int value) {
            MarginLayoutParams params = (MarginLayoutParams) object.getLayoutParams();
            params.topMargin = value;
            object.requestLayout();
        }
    };

    private static final FloatPropertyCompat SPRING_MARGIN_TOP = new FloatPropertyCompat<View>("margin_top") {

        @Override
        public float getValue(View object) {
            MarginLayoutParams params = (MarginLayoutParams) object.getLayoutParams();
            return params.topMargin;
        }

        @Override
        public void setValue(View object, float value) {
            MarginLayoutParams params = (MarginLayoutParams) object.getLayoutParams();
            params.topMargin = (int) value;
            object.requestLayout();
        }
    };
}
