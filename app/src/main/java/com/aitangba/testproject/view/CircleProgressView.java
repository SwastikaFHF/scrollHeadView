package com.aitangba.testproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by fhf11991 on 2018/8/14
 */
public class CircleProgressView extends View {

    private static final int TEXT_SIZE_TITLE = 10; // sp 文字大小
    private static final int TEXT_SIZE_SECOND_TITLE = 12; // sp 文字大小
    private static final int STROKE_WIDTH = 4; // dp

    private String mTitle = "测试";
    private String mSecondaryTitle = "测试";
    private float mPercentage = 0.5F;
    private int mStrokeWidth;
    private RectF mOval;
    private SweepGradient mSweepGradient;
    private int mNormalColor = Color.parseColor("#4dffffff");
    private Paint mCirclePaint;
    private Paint mTitlePaint;
    private int mBackgroundColor = Color.parseColor("#4dffffff");
    private int mForegroundColor = Color.parseColor("#ccffffff");

    private float mTitleSize;
    private float mSecondTitleSize;

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mStrokeWidth = dp2px(context, STROKE_WIDTH);
        mTitleSize = sp2px(context, TEXT_SIZE_TITLE);
        mSecondTitleSize = sp2px(context, TEXT_SIZE_SECOND_TITLE);

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStrokeWidth(mStrokeWidth);
        mCirclePaint.setStyle(Paint.Style.STROKE);

        mTitlePaint = new Paint();
        mTitlePaint.setColor(ContextCompat.getColor(context, android.R.color.white));
        mTitlePaint.setAntiAlias(true);

        mOval = new RectF();
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setSecondaryTitle(String secondaryTitle) {
        mSecondaryTitle = secondaryTitle;
    }

    public void setPercentage(float percentage) {
        percentage = Math.max(0, percentage);
        percentage = Math.min(100, percentage);
        mPercentage = percentage / 100;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mOval.left = mStrokeWidth / 2;
        mOval.top = mStrokeWidth / 2;
        mOval.right = w - mStrokeWidth / 2;
        mOval.bottom = h - mStrokeWidth / 2;

        // 环形颜色填充
        int startColor = Color.parseColor("#ccffffff");
        mSweepGradient = new SweepGradient(w / 2, h / 2, new int[]{startColor, mNormalColor}, new float[]{0F, 0.5F});
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int height = getMeasuredHeight();
        int width = getMeasuredWidth();

        // draw background
        mCirclePaint.setStrokeCap(Paint.Cap.BUTT);
        mCirclePaint.setColor(mBackgroundColor);
        canvas.drawArc(mOval, 0, 360, false, mCirclePaint);

        // draw foregroundColor
        mCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        mCirclePaint.setColor(mForegroundColor);
        canvas.drawArc(mOval, -90, mPercentage * 360, false, mCirclePaint);

        mTitlePaint.setTextSize(mTitleSize);
        Paint.FontMetrics titleFontMetrics = mTitlePaint.getFontMetrics();
        float absTextHeight = Math.abs(titleFontMetrics.bottom - titleFontMetrics.top);
        float absBaseLineHeight = Math.abs(titleFontMetrics.top);
        float absTextWidth = mTitlePaint.measureText(mTitle);
        canvas.drawText(mTitle, width / 2 - absTextWidth / 2, height / 2 - (absTextHeight - absBaseLineHeight), mTitlePaint);

        mTitlePaint.setTextSize(mSecondTitleSize);
        Paint.FontMetrics secondTitleFontMetrics = mTitlePaint.getFontMetrics();
        absBaseLineHeight = Math.abs(secondTitleFontMetrics.top);
        absTextWidth = mTitlePaint.measureText(mSecondaryTitle);
        canvas.drawText(mSecondaryTitle, width / 2 - absTextWidth / 2, height / 2 + absBaseLineHeight, mTitlePaint);
    }

    private static int dp2px(Context context, float dpValue) {
        final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getApplicationContext().getResources().getDisplayMetrics());
    }
}
