package com.aitangba.testproject.view;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SlideTabLayout extends View {

    private static final long sIndicatorAnimDuration = 200L;
    private static final int sBgColor = Color.parseColor("#EDF0F5");
    private static final int sIndicatorColor = Color.parseColor("#ffffff");
    private static final int sTextColorSelected = Color.parseColor("#448AFF");
    private static final int sTextColorUnselected = Color.parseColor("#333333");
    private static final float sTextSize = 20.0f; // sp 字体大小
    private static final float sGapHeight = 3.0f; // dp 间隙高度

    private RectF mBgRectF = new RectF();
    private GradientDrawable mIndicatorDrawable = new GradientDrawable();
    private ValueAnimator mValueAnimator;
    private Paint mTextPaint;
    private Paint mBgPaint;

    private String[] mTitles;
    private int mCurrentTab = 0;
    private int mIndicatorStartX = 0;
    private boolean mClickEventAvailable;

    public SlideTabLayout(Context context) {
        this(context, null);
    }

    public SlideTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBgPaint = new Paint();
        mBgPaint.setColor(sBgColor);
        mBgPaint.setAntiAlias(true);

        mIndicatorDrawable.setColor(sIndicatorColor);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(sTextColorSelected);
        mTextPaint.setTextSize(sp2px(getContext(), sTextSize));
    }

    public void setTitles(@NonNull String[] titles) {
        mTitles = titles;
        invalidate();
    }

    public void setCurrentTab(int i) {
        if (i < 0 || mTitles == null || mTitles.length == 0 || i > (mTitles.length - 1)) {
            return;
        }

        if (mCurrentTab == i) {
            invalidate();
            return;
        }

        final int gapHeightPx = dp2px(getContext(), sGapHeight);
        final int indicatorWidth = (getMeasuredWidth() - gapHeightPx * 2 - getPaddingLeft() - getPaddingRight()) / mTitles.length;
        final int targetX = getPaddingLeft() + gapHeightPx + i * indicatorWidth;
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
        }
        mValueAnimator = ValueAnimator.ofObject(new IntEvaluator(), mIndicatorStartX, targetX);
        mValueAnimator.setDuration(sIndicatorAnimDuration);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mIndicatorStartX = (Integer) animation.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimator.start();

        mCurrentTab = i;

        if (null != mListener) {
            mListener.onTabSelect(mCurrentTab);
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBgRectF.set(0, 0, w, h);

        if (mTitles == null || mTitles.length == 0) {
            mIndicatorStartX = 0;
        } else {
            final int gapHeightPx = dp2px(getContext(), sGapHeight);
            final int tabWidth = (w - gapHeightPx * 2 - getPaddingLeft() - getPaddingRight()) / mTitles.length;
            mIndicatorStartX = getPaddingLeft() + gapHeightPx + tabWidth * mCurrentTab;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int height = getMeasuredHeight();
        final int width = getMeasuredWidth();
        final int gapHeightPx = dp2px(getContext(), sGapHeight);

        // draw background
        canvas.drawRoundRect(mBgRectF, (float) height / 2, (float) height / 2, mBgPaint);

        if (mTitles == null || mTitles.length == 0) {
            return;
        }

        final int indicatorWidth = (getMeasuredWidth() - gapHeightPx * 2 - getPaddingLeft() - getPaddingRight()) / mTitles.length;

        // draw indicator
        final float indicatorCornerRadius = (width - gapHeightPx * 2) / 2.0f;
        int left = mIndicatorStartX;
        mIndicatorDrawable.setBounds(left
                , getPaddingTop() + gapHeightPx
                , left + indicatorWidth
                , height - gapHeightPx - getPaddingBottom());
        mIndicatorDrawable.setCornerRadius(indicatorCornerRadius);
        mIndicatorDrawable.draw(canvas);

        // draw text
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        final float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        final float baseline = (height / 2.0f) + distance;
        int tempX = getPaddingLeft() + gapHeightPx;
        for (int i = 0; i < mTitles.length; i++) {
            int textWidth = (int) mTextPaint.measureText(mTitles[i]);
            float centerX = tempX + (indicatorWidth - textWidth) / 2.0f;
            mTextPaint.setColor(i == mCurrentTab ? sTextColorSelected : sTextColorUnselected);
            canvas.drawText(mTitles[i], centerX, baseline, mTextPaint);
            tempX += indicatorWidth;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mClickEventAvailable = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                mClickEventAvailable = false;
                break;
            case MotionEvent.ACTION_UP:
                if (mClickEventAvailable) {
                    if (mTitles != null && mTitles.length > 0) {
                        final int gapHeightPx = dp2px(getContext(), sGapHeight);
                        final int indicatorWidth = (getMeasuredWidth() - gapHeightPx * 2 - getPaddingLeft() - getPaddingRight()) / mTitles.length;
                        int position = (int) ((event.getX() - gapHeightPx - getPaddingLeft()) / indicatorWidth);
                        setCurrentTab(position);
                    }
                    mClickEventAvailable = false;
                }
                break;
        }
        return true;
    }

    public interface OnTabSelectListener {
        void onTabSelect(int position);
    }

    private OnTabSelectListener mListener;

    public void setOnTabSelectListener(OnTabSelectListener listener) {
        this.mListener = listener;
    }

    private int dp2px(Context context, float dpValue) {
        final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int sp2px(Context context, float spValue) {
        final float fontScale = context.getApplicationContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
