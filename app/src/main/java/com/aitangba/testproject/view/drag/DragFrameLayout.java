package com.aitangba.testproject.view.drag;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.animation.DynamicAnimation;
import android.support.animation.FloatPropertyCompat;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewParentCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.IntProperty;
import android.util.Log;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by fhf11991 on 2018/8/6
 */
public class DragFrameLayout extends FrameLayout {

    private static final String TAG = "DragFrameLayout";
    private ImageView mImageView;
    private ObserverSizeTextView mTextView;
    private NestedScrollView mNestedScrollView;
    private int mLastMotionY;

    public DragFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public DragFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bindImageView(ImageView imageView) {
        mImageView = imageView;
    }

    public void setNestedScrollView(NestedScrollView nestedScrollView) {
        mNestedScrollView = nestedScrollView;
    }

    public void bindTextView(ObserverSizeTextView textView) {
        mTextView = textView;
        mTextView.setOnSizeChangedListener(new ObserverSizeTextView.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(ObserverSizeTextView textView, int w, int h, int oldw, int oldh) {
                MarginLayoutParams params = (MarginLayoutParams) textView.getLayoutParams();
                params.topMargin = (int) (dp2px(textView.getContext(), 44 + 100 / 2) - h / 2);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int actionMasked = ev.getActionMasked();
        final int actionIndex = ev.getActionIndex();
        if(actionIndex != 0) {
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
                final int minTopMargin = (int) dp2px(getContext(), 44); // 132
                final int middleTopMargin = (int) dp2px(getContext(), 100 + 44); // 432
                final int maxTopMargin = (int) dp2px(getContext(), 100 + 44 + 100); //732
                MarginLayoutParams params = (MarginLayoutParams) mNestedScrollView.getLayoutParams();

                Log.d(TAG, "dispatchTouchEvent --- "
                        + "  dy = " + dy
                        + "  topMargin = " + params.topMargin
                        + "  scrollY = " + mNestedScrollView.getScrollY());

                if (mNestedScrollView.getScrollY() == 0) {
                    if (params.topMargin == minTopMargin) {
                        if (dy > 0) {
                            return super.dispatchTouchEvent(ev);
                        } else {
                            params.topMargin = Math.max(minTopMargin, params.topMargin - dy);
                            params.topMargin = Math.min(maxTopMargin, params.topMargin);

                            mNestedScrollView.requestLayout();
                            refreshViews(dy, middleTopMargin, params.topMargin);
                            return true;
                        }
                    } else if (params.topMargin > minTopMargin && params.topMargin < maxTopMargin) {
                        params.topMargin = Math.max(minTopMargin, params.topMargin - dy);
                        params.topMargin = Math.min(maxTopMargin, params.topMargin);

                        mNestedScrollView.requestLayout();
                        refreshViews(dy, middleTopMargin, params.topMargin);
                        return true;
                    } else if (params.topMargin == maxTopMargin) {
                        if (dy > 0) {
                            params.topMargin = Math.max(minTopMargin, params.topMargin - dy);
                            params.topMargin = Math.min(maxTopMargin, params.topMargin);

                            mNestedScrollView.requestLayout();
                            refreshViews(dy, middleTopMargin, params.topMargin);
                            return true;
                        } else {
                            return super.dispatchTouchEvent(ev);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                recoveryViews(getContext());
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    private void refreshViews(int dy, int middleTopMargin, int topMargin) {
        if (topMargin <= middleTopMargin && topMargin >= dp2px(getContext(), 72)) {
            refreshImageView(getContext(), dy);
            refreshTextView(getContext(), dy);
        }
    }

    private void refreshImageView(Context context, int dyConsumed) {
        final float scaleFactor = 0.4F;
        final float maxImageTranslationY = dp2px(context, 44 / 2 + 100 / 2);
        final float maxImageTranslationX = dp2px(context, 100 / 2 - 40 / 2);
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

    private void refreshTextView(Context context, int dyConsumed) {
        final float scaleFactor = 0.8F;
        final float maxTextTranslationY = dp2px(context, 44 / 2 + 100 / 2);
        final int width = mTextView.getMeasuredWidth();
        final float maxTextTranslationX = getMeasuredWidth() / 2 - dp2px(context, 100) - width / 2 - (1 - scaleFactor) / 2 * width;

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

    private void recoveryViews(Context context) {
        final int minTopMargin = (int) dp2px(getContext(), 44); // 132
        final int middleTopMargin = (int) dp2px(getContext(), 100 + 44); // 432

        final int limitMargin = (int) dp2px(getContext(), 100 / 2 + 44); // 432

        MarginLayoutParams params = (MarginLayoutParams) mNestedScrollView.getLayoutParams();
        if (params.topMargin > minTopMargin && params.topMargin <= limitMargin) {
            ObjectAnimator marginTopAnim = ObjectAnimator.ofInt(mNestedScrollView, MARGIN_TOP, params.topMargin, minTopMargin);

            final float maxTranslationY = dp2px(context, 44 / 2 + 100 / 2);
            final float maxImageTranslationX = dp2px(context, 100 / 2 - 40 / 2);
            final float imageScaleFactor = 0.4F;

            ObjectAnimator imageTranXAnim = ObjectAnimator.ofFloat(mImageView, TRANSLATION_X, mImageView.getTranslationX(), -maxImageTranslationX);
            ObjectAnimator imageTranYAnim = ObjectAnimator.ofFloat(mImageView, TRANSLATION_Y, mImageView.getTranslationY(), -maxTranslationY);
            ObjectAnimator imageScaleXAnim = ObjectAnimator.ofFloat(mImageView, SCALE_X, mImageView.getScaleX(), imageScaleFactor);
            ObjectAnimator imageScaleYAnim = ObjectAnimator.ofFloat(mImageView, SCALE_Y, mImageView.getScaleY(), imageScaleFactor);

            final float textScaleFactor = 0.8F;
            final int width = mTextView.getMeasuredWidth();
            final float maxTextTranslationX = getMeasuredWidth() / 2 - dp2px(context, 100) - width / 2 - (1 - textScaleFactor) / 2 * width;

            ObjectAnimator textTranXAnim = ObjectAnimator.ofFloat(mTextView, TRANSLATION_X, mTextView.getTranslationX(), maxTextTranslationX);
            ObjectAnimator textTranYAnim = ObjectAnimator.ofFloat(mTextView, TRANSLATION_Y, mTextView.getTranslationY(), -maxTranslationY);
            ObjectAnimator textScaleXAnim = ObjectAnimator.ofFloat(mTextView, SCALE_X, mTextView.getScaleX(), textScaleFactor);
            ObjectAnimator textScaleYAnim = ObjectAnimator.ofFloat(mTextView, SCALE_Y, mTextView.getScaleY(), textScaleFactor);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(200);
            animatorSet.setInterpolator(new DecelerateInterpolator(2f));
            animatorSet.playTogether(marginTopAnim,
                    imageTranXAnim, imageTranYAnim, imageScaleXAnim, imageScaleYAnim,
                    textTranXAnim, textTranYAnim, textScaleXAnim, textScaleYAnim);
            animatorSet.start();

        } else if (params.topMargin > limitMargin && params.topMargin != middleTopMargin) {
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

            final int maxTopMargin = (int) dp2px(getContext(), 100 + 44 + 100); //732
            if(params.topMargin - middleTopMargin > (maxTopMargin - middleTopMargin) * 0.4) {
                SpringAnimation anim = new SpringAnimation(mNestedScrollView, SPRING_MARGIN_TOP, middleTopMargin);
                anim.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
                anim.setStartValue(params.topMargin);
                anim.start();

                animatorSet.playTogether(
                        imageTranXAnim, imageTranYAnim, imageScaleXAnim, imageScaleYAnim,
                        textTranXAnim, textTranYAnim, textScaleXAnim, textScaleYAnim);
                animatorSet.start();
            } else {
                ObjectAnimator marginTopAnim = ObjectAnimator.ofInt(mNestedScrollView, MARGIN_TOP, params.topMargin, middleTopMargin);
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
