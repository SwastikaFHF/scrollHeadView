package com.aitangba.testproject.view.drag;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.animation.FloatPropertyCompat;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.IntProperty;
import android.util.Log;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by fhf11991 on 2018/8/6
 */
public class DragFrameLayout extends FrameLayout {

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

        mMinTopMargin = mHeadViewHeight; // 132
        mMiddleTopMargin = mHeadViewHeight + mMiddleSpaceHeight;// 432
        mMaxTopMargin = mHeadViewHeight + mMiddleSpaceHeight + mHideSpaceHeight;  //732
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int actionMasked = ev.getActionMasked();
        final int actionIndex = ev.getActionIndex();
        if (actionIndex != 0) {
            return true;
        }
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = (int) ev.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                final int y = (int) ev.getRawY();
                int dy = mLastMotionY - y;
                mLastMotionY = y;
                MarginLayoutParams params = (MarginLayoutParams) mNestedScrollView.getLayoutParams();

                Log.d(TAG, "dispatchTouchEvent --- "
                        + "  dy = " + dy
                        + "  topMargin = " + params.topMargin
                        + "  scrollY = " + mNestedScrollView.getScrollY());

                if (mNestedScrollView.getScrollY() == 0) {
                    if (params.topMargin == mMinTopMargin) {
                        if (dy > 0) {
                            return super.dispatchTouchEvent(ev);
                        } else {
                            params.topMargin = Math.max(mMinTopMargin, params.topMargin - dy);
                            params.topMargin = Math.min(mMaxTopMargin, params.topMargin);

                            mNestedScrollView.requestLayout();
                            refreshViews(dy, params.topMargin);
                            return true;
                        }
                    } else if (params.topMargin > mMinTopMargin && params.topMargin < mMaxTopMargin) {
                        params.topMargin = Math.max(mMinTopMargin, params.topMargin - dy);
                        params.topMargin = Math.min(mMaxTopMargin, params.topMargin);

                        mNestedScrollView.requestLayout();
                        refreshViews(dy, params.topMargin);
                        return true;
                    } else if (params.topMargin == mMaxTopMargin) {
                        if (dy > 0) {
                            params.topMargin = Math.max(mMinTopMargin, params.topMargin - dy);
                            params.topMargin = Math.min(mMaxTopMargin, params.topMargin);

                            mNestedScrollView.requestLayout();
                            refreshViews(dy, params.topMargin);
                            return true;
                        } else {
                            return super.dispatchTouchEvent(ev);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                recoveryViews();
                break;
        }

        return super.dispatchTouchEvent(ev);
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
        final int limitMargin = mMiddleSpaceHeight / 2 + mHeadViewHeight;

        MarginLayoutParams params = (MarginLayoutParams) mNestedScrollView.getLayoutParams();
        if (params.topMargin > mMinTopMargin && params.topMargin <= limitMargin) {
            ObjectAnimator marginTopAnim = ObjectAnimator.ofInt(mNestedScrollView, MARGIN_TOP, params.topMargin, mMinTopMargin);

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

        } else if (params.topMargin > limitMargin && params.topMargin != mMiddleTopMargin) {
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

            if (params.topMargin - mMiddleTopMargin > (mMaxTopMargin - mMiddleTopMargin) * 0.4) {
                SpringAnimation anim = new SpringAnimation(mNestedScrollView, SPRING_MARGIN_TOP, mMiddleTopMargin);
                anim.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
                anim.setStartValue(params.topMargin);
                anim.start();

                animatorSet.playTogether(
                        imageTranXAnim, imageTranYAnim, imageScaleXAnim, imageScaleYAnim,
                        textTranXAnim, textTranYAnim, textScaleXAnim, textScaleYAnim);
                animatorSet.start();
            } else {
                ObjectAnimator marginTopAnim = ObjectAnimator.ofInt(mNestedScrollView, MARGIN_TOP, params.topMargin, mMiddleTopMargin);
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

    private static final FloatPropertyCompat SPRING_MARGIN_TOP = new FloatPropertyCompat<NestedScrollView>("margin_top") {

        @Override
        public float getValue(NestedScrollView object) {
            MarginLayoutParams params = (MarginLayoutParams) object.getLayoutParams();
            return params.topMargin;
        }

        @Override
        public void setValue(NestedScrollView object, float value) {
            MarginLayoutParams params = (MarginLayoutParams) object.getLayoutParams();
            params.topMargin = (int) value;
            object.requestLayout();
        }
    };
}
