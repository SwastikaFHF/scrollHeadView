package com.aitangba.testproject.view.drag;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.animation.DynamicAnimation;
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
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by fhf11991 on 2018/8/6
 */
public class DragFrameLayout extends FrameLayout implements NestedScrollingParent {

    private static final String TAG = "DragFrameLayout";
    private ImageView mImageView;
    private ObserverSizeTextView mTextView;
    private View mCardView;

    public DragFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public DragFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private int mTouchSlop;
    public DragFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mTopMargin = (int) dp2px(context, 144);

        final ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
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
                params.topMargin = (int) (dp2px(textView.getContext(), 44 + 100 / 2) - h / 2);
            }
        });
    }

    public void setCardView(View cardView) {
        mCardView = cardView;
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return target instanceof NestedScrollView;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
    }

    private int mTopMargin;
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {

        final int minTopMargin = (int) dp2px(target.getContext(), 44);
        final int maxTopMargin = (int) dp2px(target.getContext(), 44 + 100);
        MarginLayoutParams params = (MarginLayoutParams) target.getLayoutParams();
        Log.d(TAG, "onNestedPreScroll --- "
                + "  dy = " + dy
                + "  topMargin = " + params.topMargin
                + "  ScrollY = " + target.getScrollY());

        if(dy > 0 && params.topMargin == minTopMargin) {
            return;
        } else if(dy <0 && params.topMargin == maxTopMargin) {
            return;
        }

        if(dy < 0 && params.topMargin == minTopMargin && target.getScrollY() != 0) {
            return;
        }

        int consumedY = 0;
        if(dy > 0) {
            int temp = mTopMargin - Math.abs(dy);
            if(temp < minTopMargin) {
                consumedY = mTopMargin - minTopMargin;
                mTopMargin = minTopMargin;
            } else {
                consumedY = dy;
                mTopMargin = temp;
            }
        } else if(dy < 0) {
            int temp = mTopMargin + Math.abs(dy);
            if(temp > maxTopMargin) {
                consumedY = -(maxTopMargin - params.topMargin);
                mTopMargin = maxTopMargin;
            } else {
                consumedY = dy;
                mTopMargin = temp;
            }
        }

        consumed[1] = consumedY;
        params.topMargin = mTopMargin;
        target.requestLayout();
//        if(Math.abs(mTopMargin - params.topMargin) >= mTouchSlop || mTopMargin == minTopMargin || mTopMargin == maxTopMargin) {
//        }
        if(true) {
            return;
        }

        if(dy > 0) { //上划
            int temp = mTopMargin - Math.abs(dy);
            if(temp < minTopMargin) {
                consumedY = mTopMargin - minTopMargin;
                mTopMargin = minTopMargin;
            } else if(temp > minTopMargin){
                if(dy > mTouchSlop) {
                    params.topMargin = temp;
                    consumed[1] = dy;
                    mTopMargin = params.topMargin;
                }
            }
        }

        if(dy > 0) {
            int temp = mTopMargin - Math.abs(dy);
            if(temp < minTopMargin) {
                consumed[1] = (int) (params.topMargin - minTopMargin);
                params.topMargin = (int) minTopMargin;
                mTopMargin = params.topMargin;
            } else {
                if(Math.abs(mTopMargin - temp) > mTouchSlop) {
                    params.topMargin = temp;
                    consumed[1] = dy;
                    mTopMargin = params.topMargin;
                } else {
                    mTopMargin = mTopMargin - Math.abs(dy);
                    return;
                }
            }

            target.requestLayout();

//            refreshImageView(target.getContext(), dy);
//            refreshTextView(target.getContext(), dy);
        } else if(dy < 0) {
            int temp = mTopMargin + Math.abs(dy);
            if(temp > maxTopMargin) {
                consumed[1] = -(int) (maxTopMargin - params.topMargin);
                params.topMargin = (int) maxTopMargin;
            } else {
                if(Math.abs(mTopMargin - temp) > mTouchSlop) {
                    params.topMargin = temp;
                    consumed[1] = dy;
                    mTopMargin = params.topMargin;
                } else {
                    mTopMargin = mTopMargin + Math.abs(dy);
                    return;
                }
            }

            target.requestLayout();

//            refreshImageView(target.getContext(), dy);
//            refreshTextView(target.getContext(), dy);
        }
    }

    private int mScrolledY = 0; //

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        mScrolledY = mScrolledY + dyConsumed;

        int scrollY = target.getScrollY();

        Log.d(TAG, "onNestedScroll --- "
                + "  dyConsumed = " + dyConsumed
                + "  dyUnconsumed = " + dyUnconsumed
                + "  mScrolledY = " + mScrolledY
                + "  scrollY = " + scrollY);

//        final float maxTranslationY = dp2px(target.getContext(), 44 / 2 + 100 / 2);
//        if(dyConsumed !=0 && target.getScrollY() <= maxTranslationY) {
//            refreshImageView(target.getContext(), dyConsumed);
//            refreshTextView(target.getContext(), dyConsumed);
//        } else if(dyConsumed == 0) {
//            refreshCardView(target.getContext(), dyConsumed, dyUnconsumed);
//        }


//        if(dyConsumed > 0) {
//            refreshImageView(target.getContext(), dyConsumed);
//            refreshTextView(target.getContext(), dyConsumed);
//        } else if(dyConsumed < 0) {
//            if(mScrolledY > 0 && mScrolledY <= maxTranslationY) {
//                refreshImageView(target.getContext(), dyConsumed);
//                refreshTextView(target.getContext(), dyConsumed);
//            }
//        } else {
//            refreshCardView(target.getContext(), dyConsumed, dyUnconsumed);
//        }
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

    private void refreshCardView(Context context, int dyConsumed, int dyUnconsumed) {
        final float maxCardTranslationY = dp2px(context, 100);
        if (dyUnconsumed < 0 && dyConsumed == 0) { // 下拉到顶
            float translationY = Math.abs(mCardView.getTranslationY()) + Math.abs(dyUnconsumed);
            if (translationY > maxCardTranslationY) {
                translationY = maxCardTranslationY;
            }
            mCardView.setTranslationY(translationY);
        } else if(dyUnconsumed > 0 && dyConsumed == 0) {
            float translationY = Math.abs(mCardView.getTranslationY()) - Math.abs(dyUnconsumed);
            if (translationY < 0) {
                translationY = 0;
            }
            mCardView.setTranslationY(translationY);
        }
    }

    private void recoveryCardView(Context context) {
        float translationY = mCardView.getTranslationY();
        if(translationY != 0) {
            final float maxCardTranslationY = dp2px(context, 100);
            if(translationY < maxCardTranslationY * 0.4) { // 距离太短，启用简单动画
                Interpolator interpolator = new DecelerateInterpolator(2f);
                ObjectAnimator animator = ObjectAnimator.ofFloat(mCardView, TRANSLATION_Y, translationY, 0);
                animator.setInterpolator(interpolator);
                animator.setDuration(200);
                animator.start();
            } else { //启用弹性动画
                SpringAnimation anim = new SpringAnimation(mCardView, DynamicAnimation.TRANSLATION_Y, 0f);
                anim.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
                anim.setStartValue(translationY);
                anim.start();
            }
        }
    }

    private void recoveryImageAndText(Context context, View child) {
        final float maxTranslationY = dp2px(context, 44 / 2 + 100 / 2);
        float translationY = mImageView.getTranslationY();
        final float absTranslationY = Math.abs(translationY);

        if(0 < absTranslationY && absTranslationY < maxTranslationY / 2) { // 下拉
            ObjectAnimator scrollViewAnim = ObjectAnimator.ofInt(child, SCROLL_Y, child.getScrollY(), 0);

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
            animatorSet.playTogether(
                    imageTranXAnim, imageTranYAnim, imageScaleXAnim, imageScaleYAnim,
                    textTranXAnim ,textTranYAnim, textScaleXAnim, textScaleYAnim);
            animatorSet.start();


        } else if(maxTranslationY / 2 <= absTranslationY && absTranslationY < maxTranslationY) { // 上拉
            final float maxImageTranslationX = dp2px(context, 100 / 2 - 40 / 2);
            final float imageScaleFactor = 0.4F;
            ObjectAnimator scrollViewAnim = ObjectAnimator.ofInt(child, SCROLL_Y, child.getScrollY(), (int) maxTranslationY);

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
            animatorSet.playTogether(
                    imageTranXAnim, imageTranYAnim, imageScaleXAnim, imageScaleYAnim,
                    textTranXAnim ,textTranYAnim, textScaleXAnim, textScaleYAnim);
            animatorSet.start();
        }
    }

    @Override
    public void onStopNestedScroll(View child) {
        recoveryCardView(child.getContext());
        recoveryImageAndText(child.getContext(), child);
        int scrollY = child.getScrollY();

        Log.d(TAG, "onStopNestedScroll --- "
                + "  scrollY = " + scrollY);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    private static float dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5F;
    }

    public static final Property<View, Integer> SCROLL_Y = new IntProperty<View>("scaleY") {
        @Override
        public Integer get(View object) {
            return object.getScrollY();
        }

        @Override
        public void setValue(View object, int value) {
            object.setScrollY(value);
        }
    };
}
