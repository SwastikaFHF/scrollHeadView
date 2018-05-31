package com.aitangba.testproject.view.path;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * Created by fhf11991 on 2016/10/11.
 */
public class LoadingView extends View {
    private static final String TAG = "LoadingView";

    private Paint mPaint;
    private Path path_circle;
    private PathMeasure mMeasure;
    private ValueAnimator mLoadingAnimator;
    private ValueAnimator mResultAnimator;
    private int mLoadingDuration = 1000;
    private int mResultDuration = 1500;

    // 动效过程监听器
    private float mLoadingAnimatorValue = 0;
    private float mResultAnimatorValue = 0;

    private boolean mIsResultAnimatorStart;
    private int mStrokeWidth = 5;

    private int mLoadingColor = Color.parseColor("#293685");
    private int mLoadingSuccessColor = Color.parseColor("#11c876");
    private Paint mLoadingErrorPaint;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initAnimator();
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mLoadingColor);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);

        path_circle = new Path();
        mMeasure = new PathMeasure();

        mLoadingErrorPaint = new Paint();
        mLoadingErrorPaint.setStyle(Paint.Style.STROKE);
        mLoadingErrorPaint.setColor(Color.RED);
        mLoadingErrorPaint.setStrokeWidth(mStrokeWidth);
        mLoadingErrorPaint.setStrokeCap(Paint.Cap.ROUND);
        mLoadingErrorPaint.setAntiAlias(true);
    }

    private void initAnimator() {
        mLoadingAnimator = ValueAnimator.ofFloat(0, 1).setDuration(mLoadingDuration);
        mLoadingAnimator.setInterpolator(new AccelerateInterpolator(2f));
        mLoadingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLoadingAnimatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mLoadingAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                final Status status = mStatus;
                if(mStatus == Status.Loading) {
                } else if(status == Status.Success) {
                    mPaint.setColor(mLoadingSuccessColor);
                    invalidate();
                    mLoadingAnimator.cancel();
                    mResultAnimator.start();
                } else if(status == Status.Failed) {
                    mPaint.setColor(Color.RED);
                    invalidate();
                    mLoadingAnimator.cancel();
                    mResultAnimator.start();
                } else {
                    mLoadingAnimator.cancel();
                }
            }
        });
        mLoadingAnimator.setRepeatCount(ValueAnimator.INFINITE);

        mResultAnimator = ValueAnimator.ofFloat(0, 1).setDuration(mResultDuration);
        mResultAnimator.setInterpolator(new AccelerateInterpolator(4f));
        mResultAnimator.setRepeatCount(0);
        mResultAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mResultAnimatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mResultAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsResultAnimatorStart = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(mStatus == Status.Failed) {
                    shakeWarning(LoadingView.this);
                } else {
                    if(mProgressListener != null) {
                        mProgressListener.onStop(LoadingView.this);
                    }
                }
            }
        });
    }

    private int mRadius;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        mRadius = Math.min(widthSize, heightSize);
        mRadius -= 2 * mStrokeWidth;
        float left = (float) (widthSize - mRadius) / 2;
        final RectF oval2 = new RectF(left, mStrokeWidth, left + mRadius, mRadius);      // 外部圆环
        path_circle.reset();
        path_circle.addArc(oval2, -90, 359.9f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //draw circle
        if(mIsResultAnimatorStart) {
            canvas.drawPath(path_circle, mPaint);
        } else {
            final float loadingValue = mLoadingAnimatorValue;
            final float length = mMeasure.getLength();
            Path circlePartPath = getPartPath();
            mMeasure.setPath(path_circle, false);
            mMeasure.getSegment(0, length * loadingValue, circlePartPath, true);
            canvas.drawPath(circlePartPath, mPaint);
            return;
        }

        //draw result
        final float resultValue = mResultAnimatorValue;
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        final float halfWidth = width / 2;
        final float halfHeight = height / 2;

        final float radius = mRadius * 0.8f;
        switch (mStatus) {
            case Success:
                final float correctDiff = -4;
                final float quarterRadius = radius / 4;

                Path hookPath = new Path();
                hookPath.moveTo(halfWidth - quarterRadius + correctDiff, halfHeight);
                hookPath.lineTo(halfWidth + correctDiff, halfHeight + quarterRadius);
                hookPath.lineTo(halfWidth + radius * 7 / 16 + correctDiff, halfHeight - quarterRadius);

                final Path hookPartPath = getPartPath();

                mMeasure.setPath(hookPath, false);
                final float hookLength = mMeasure.getLength();
                mMeasure.getSegment(0, hookLength * resultValue, hookPartPath, true);

                canvas.drawPath(hookPartPath, mPaint);

                break;
            case Failed:
                float totalLength = radius * 11 / 16 + 1;
                float startY = halfHeight - radius * 3 / 8;
                float currentLength = resultValue * totalLength;

                Path failedPath = new Path();
                failedPath.moveTo(halfWidth, startY);
                failedPath.lineTo(halfWidth, startY + currentLength);

                mLoadingErrorPaint.setPathEffect(new DashPathEffect(new float[]{radius * 8 / 16, radius * 3 / 16}, 0));
                canvas.drawPath(failedPath, mLoadingErrorPaint);
                break;
            default:break;
        }
    }

    private Path getPartPath() {
        final Path partPath = new Path();
        partPath.reset();
        // 硬件加速的BUG
        partPath.lineTo(0,0);
        return partPath;
    }

    public void setStatus(Status status) {
        mStatus = status;

        if(mStatus == Status.Loading) {
            mIsResultAnimatorStart = false;
            mPaint.setColor(mLoadingColor);
            mLoadingAnimator.start();
        } else if(mStatus == Status.Cancel) {
            mLoadingAnimator.cancel();
        }
    }

    private Status mStatus = Status.Loading;
    public enum Status {
        Loading, Success, Failed, Cancel
    }

    private void shakeWarning(View shakeView) {
        Animation animation = new TranslateAnimation(0, 10, 0, 0);
        animation.setInterpolator(new CycleInterpolator(4));
        animation.setDuration(1300);
        shakeView.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(mProgressListener != null) {
                    mProgressListener.onStop(LoadingView.this);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private ProgressListener mProgressListener;

    public void setProgressListener(ProgressListener progressListener) {
        mProgressListener = progressListener;
    }

    public interface ProgressListener {
        void onStop(LoadingView loadingView);
    }
}
