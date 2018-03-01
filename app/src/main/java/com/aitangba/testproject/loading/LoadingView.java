package com.aitangba.testproject.loading;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by fhf11991 on 2018/2/26.
 */

public class LoadingView extends View {

    private final static int RADIUS = 20;
    private final static int TARGET_VALUE = 150;
    private final static int DURATION = 600;

    private final AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator(); // 加速
    private final DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(); // 减速
    private Paint mRedPaint;
    private Paint mYellowPaint;
    private Paint mGreenPaint;
    private ValueAnimator mValueAnimator;
    private int mCurrentCycle = 0; // 共5个周期
    private int mCurrentValue = 0;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mRedPaint = new Paint();
        mRedPaint.setColor(Color.parseColor("#EC6173"));
        mRedPaint.setAntiAlias(true);

        mYellowPaint = new Paint();
        mYellowPaint.setColor(Color.parseColor("#FFE793"));
        mYellowPaint.setAntiAlias(true);

        mGreenPaint = new Paint();
        mGreenPaint.setColor(Color.parseColor("#74DF74"));
        mGreenPaint.setAntiAlias(true);

        mValueAnimator = buildAnimator();
        mValueAnimator.start();

        addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                start();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                cancel();
            }
        });
    }

    private ValueAnimator buildAnimator() {
        ValueAnimator animator = ValueAnimator.ofInt(1, TARGET_VALUE);
        animator.setDuration(DURATION);
        animator.setInterpolator(mCurrentCycle % 2 == 0 ? accelerateInterpolator : decelerateInterpolator);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mCurrentCycle = (mCurrentCycle + 1) % 6;
                mValueAnimator = buildAnimator();
                mValueAnimator.start();
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentValue = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        return animator;
    }

    public void start() {
        if(mValueAnimator != null) {
            mValueAnimator.start();
        }
    }

    public void cancel() {
        if(mValueAnimator != null) {
            mValueAnimator.cancel();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        int width = getMeasuredWidth();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();

        int totalDistance = (width - paddingLeft - paddingRight) / 2 - RADIUS;
        int distance = mCurrentValue * totalDistance / TARGET_VALUE;
        int middleCenterX = paddingLeft + totalDistance + RADIUS;

        switch (mCurrentCycle) {
            case 0:
                // red circle  ->|
                canvas.drawCircle(paddingLeft + RADIUS + distance, paddingTop + RADIUS, RADIUS, mRedPaint);

                // yellow circle
                canvas.drawCircle(middleCenterX, paddingTop + RADIUS, RADIUS, mYellowPaint);

                // green circle |<-
                canvas.drawCircle(width - paddingRight - RADIUS - distance, paddingTop + RADIUS, RADIUS, mGreenPaint);
                break;
            case 1:
                // red circle |->
                canvas.drawCircle(paddingLeft + totalDistance + RADIUS + distance, paddingTop + RADIUS, RADIUS, mRedPaint);

                // yellow circle
                canvas.drawCircle(middleCenterX, paddingTop + RADIUS, RADIUS, mYellowPaint);

                // green circle <-|
                canvas.drawCircle(paddingLeft + totalDistance + RADIUS - distance, paddingTop + RADIUS, RADIUS, mGreenPaint);
                break;
            case 2:
                // red circle |<-
                canvas.drawCircle(width - paddingRight - RADIUS - distance, paddingTop + RADIUS, RADIUS, mRedPaint);

                // yellow circle
                canvas.drawCircle(middleCenterX, paddingTop + RADIUS, RADIUS, mYellowPaint);

                // green circle ->|
                canvas.drawCircle(paddingLeft + RADIUS + distance, paddingTop + RADIUS, RADIUS, mGreenPaint);
                break;
            case 3:
                // red circle
                canvas.drawCircle(middleCenterX, paddingTop + RADIUS, RADIUS, mRedPaint);

                // yellow circle <-|
                canvas.drawCircle(paddingLeft + totalDistance + RADIUS - distance, paddingTop + RADIUS, RADIUS, mYellowPaint);

                // green circle |->
                canvas.drawCircle(paddingLeft + totalDistance + RADIUS + distance, paddingTop + RADIUS, RADIUS, mGreenPaint);
                break;
            case 4:
                // red circle
                canvas.drawCircle(middleCenterX, paddingTop + RADIUS, RADIUS, mRedPaint);

                // yellow circle ->|
                canvas.drawCircle(paddingLeft + RADIUS + distance, paddingTop + RADIUS, RADIUS, mYellowPaint);

                // green circle |<-
                canvas.drawCircle(width - paddingRight - RADIUS - distance, paddingTop + RADIUS, RADIUS, mGreenPaint);
                break;
            case 5:
                // red circle <-|
                canvas.drawCircle(paddingLeft + totalDistance + RADIUS - distance, paddingTop + RADIUS, RADIUS, mRedPaint);

                // yellow circle
                canvas.drawCircle(middleCenterX, paddingTop + RADIUS, RADIUS, mYellowPaint);

                // green circle |->
                canvas.drawCircle(paddingLeft + totalDistance + RADIUS + distance, paddingTop + RADIUS, RADIUS, mGreenPaint);
                break;

        }
    }
}
