package com.aitangba.testproject.view.drag;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.IntProperty;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by fhf11991 on 2018/8/6
 */
public class DragFrameLayout extends FrameLayout implements NestedScrollingParent {

    private static final String TAG = "DragFrameLayout";

    private static final float HEAD_VIEW_HEIGHT = 44F; // dp
    private static final float MIDDLE_SPACE_HEIGHT = 100F; // dp
    private static final float HIDE_SPACE_HEIGHT = 100F; // dp
    private static final float IMAGE_RADIUS = 40F; // dp

    private ImageView mImageView;
    private ObserverSizeTextView mTextView;
    private NestedScrollView mNestedScrollView;

    private int mLastMotionY;

    private final int mMinTopMargin;
    private final int mMiddleTopMargin;
    private final int mMaxTopMargin;

    private final int mHeadViewHeight;
    private final int mMiddleSpaceHeight;
    private final int mHideSpaceHeight;
    private final int mImageRadius;

    public DragFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public DragFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mImageRadius = (int) dp2px(context, IMAGE_RADIUS);

        mHeadViewHeight = (int) dp2px(context, HEAD_VIEW_HEIGHT);
        mMiddleSpaceHeight = (int) dp2px(context, MIDDLE_SPACE_HEIGHT);
        mHideSpaceHeight = (int) dp2px(context, HIDE_SPACE_HEIGHT);

        mMinTopMargin = 0; // 132
        mMiddleTopMargin = mMiddleSpaceHeight;// 432
        mMaxTopMargin = mMiddleSpaceHeight + mHideSpaceHeight;  //732
    }

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

    public void bindNestedScrollView(NestedScrollView nestedScrollView) {
        mNestedScrollView = nestedScrollView;
    }

    private void onDragging(MarginLayoutParams params, int dy) {
        params.topMargin = Math.max(mMinTopMargin, params.topMargin - dy);
        params.topMargin = Math.min(mMaxTopMargin, params.topMargin);

        mNestedScrollView.getChildAt(0).requestLayout();
//        refreshViews(dy, params.topMargin);
//        final ViewParent parent = getParent();
//        if (parent != null) {
//            parent.requestDisallowInterceptTouchEvent(true);
//        }
    }

    private void refreshViews(int dy, int topMargin) {
        if (topMargin <= mMiddleTopMargin && topMargin >= mMiddleSpaceHeight - mImageRadius - mHeadViewHeight / 2 + mHeadViewHeight) {
            refreshImageView(dy);
            refreshTextView(dy);
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
        final int limitMargin = mImageRadius;

        View view = mNestedScrollView;
        MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
        final int topMargin = params.topMargin;
        Log.d(TAG, "recoveryViews ------ "
                + " params.topMargin = " + topMargin
                + " limitMargin = " + limitMargin
                + " mMiddleTopMargin = " + mMiddleTopMargin);
        if (topMargin <= limitMargin) {
            ObjectAnimator marginTopAnim = ObjectAnimator.ofInt(view, MARGIN_TOP, topMargin, mMinTopMargin);

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

            if (topMargin - mMiddleTopMargin > (mMaxTopMargin - mMiddleTopMargin) * 0.4) {
                SpringAnimation anim = new SpringAnimation(view, SPRING_MARGIN_TOP, mMiddleTopMargin);
                anim.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
                anim.setStartValue(topMargin);
                anim.start();

                animatorSet.playTogether(
                        imageTranXAnim, imageTranYAnim, imageScaleXAnim, imageScaleYAnim,
                        textTranXAnim, textTranYAnim, textScaleXAnim, textScaleYAnim);
                animatorSet.start();
            } else {
                ObjectAnimator marginTopAnim = ObjectAnimator.ofInt(view, MARGIN_TOP, topMargin, mMiddleTopMargin);
                animatorSet.playTogether(marginTopAnim,
                        imageTranXAnim, imageTranYAnim, imageScaleXAnim, imageScaleYAnim,
                        textTranXAnim, textTranYAnim, textScaleXAnim, textScaleYAnim);
                animatorSet.start();
            }
        }
    }

    private static float dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5F;
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


    /**
     * 回调开始滑动
     *
     * @param child            该父VIew 的子View
     * @param target           支持嵌套滑动的 VIew
     * @param nestedScrollAxes 滑动方向
     * @return 是否支持 嵌套滑动
     */
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
    }

    @Override
    public void onStopNestedScroll(View target) {
        recoveryViews();
    }

    /**
     * 这里 主要处理 dyUnconsumed dxUnconsumed 这两个值对应的数据
     *
     * @param target
     * @param dxConsumed
     * @param dyConsumed
     * @param dxUnconsumed
     * @param dyUnconsumed
     */
    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
    }

    private int screenY = 0;
    private boolean offScreen = false;
    /**
     * 这里 传来了 x y 方向上的滑动距离
     * 并且 先与 子VIew  处理滑动,  并且 consumed  中可以设置相应的 除了的距离
     * 然后 子View  需要更具这感觉, 来处理自己滑动
     *
     * @param target
     * @param dx
     * @param dy
     * @param consumed
     */
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        MarginLayoutParams params = (MarginLayoutParams) mNestedScrollView.getChildAt(0).getLayoutParams();
        Log.d(TAG, "onNestedPreScroll "
                + " dy = " + dy
                + " topMargin = " + params.topMargin
                + " ScrollY = " + mNestedScrollView.getScrollY());
        int diffDy = dy - screenY;

        if(params.topMargin == mMinTopMargin) {
            if(dy > 0) {
                consumed[1] = screenY;
            } else {
                onDragging(params, offScreen ? diffDy : dy);
                consumed[1] = dy;
                screenY = dy;
            }
            offScreen = false;
        } else if(params.topMargin == mMaxTopMargin) {
            if(dy > 0) {
                onDragging(params, offScreen ? diffDy : dy);
                consumed[1] = dy;
                screenY = dy;
            } else {
                if(offScreen) {
                    consumed[1] = screenY;
                }
            }
            offScreen = false;
        } else {
            offScreen = true;
            onDragging(params, diffDy);
            consumed[1] = dy;
            screenY = dy;
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return 1 << 1;
    }

}
