package com.aitangba.testproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by fhf11991 on 2018/8/3
 */
public class NumberProgressView extends View {

    private static final float BIGGER_TEXT_SIZE = 20; //sp
    private static final float SMALLER_TEXT_SIZE = 18; //sp
    private static final float BAND_WIDTH = 3; //dp
    private Paint mBgPaint;
    private Paint mTextPaint;
    private int mBiggerTextSize;
    private int mSmallTextSize;
    private int mCurrentNumber = 20;
    private int mTotalNumber = 20;
    private int mBandWidth;

    public NumberProgressView(Context context) {
        this(context, null);
    }

    public NumberProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mBiggerTextSize = sp2px(context, BIGGER_TEXT_SIZE);
        mSmallTextSize = sp2px(context, SMALLER_TEXT_SIZE);
        mBandWidth = dp2px(context, BAND_WIDTH);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setStyle(Paint.Style.STROKE);
//        mTextPaint.setStrokeWidth(10);

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(Color.RED);
        mBgPaint.setStyle(Paint.Style.FILL);
    }

    public void setCurrentNumber(int currentNumber) {
        mCurrentNumber = currentNumber;
        invalidate();
    }

    public void setTotalNumber(int totalNumber) {
        mTotalNumber = totalNumber;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int radius = Math.min(width, height) / 2;
        int centX = width / 2;
        int centY = height / 2;

        mBgPaint.setColor(Color.RED);
        mBgPaint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(centX, centY, radius, mBgPaint);

        mBgPaint.setColor(Color.WHITE);
        mBgPaint.setStyle(Paint.Style.STROKE);

        mBgPaint.setStrokeWidth(mBandWidth);
        canvas.drawCircle(centX, centY, radius - 2 * mBandWidth, mBgPaint);

        int dis = (int) (radius *  2 / 4 * Math.cos(Math.PI *  45 / 180));
        mBgPaint.setStrokeWidth(6);
        canvas.drawLine(centX + dis, centY - dis, centX - dis, centY + dis, mBgPaint);

        int offset = mBandWidth;

        String currentNumber = String.valueOf(mCurrentNumber);
        mTextPaint.setTextSize(mBiggerTextSize);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        int textWidth = (int) mTextPaint.measureText(currentNumber);
        canvas.drawText(String.valueOf(mCurrentNumber), centX - 0.2f * offset - textWidth, centY - 1.2f * offset, mTextPaint);

        mTextPaint.setTextSize(mSmallTextSize);
        mTextPaint.setTypeface(Typeface.DEFAULT);
        int bottom = (int) mTextPaint.getFontMetrics().bottom;
        canvas.drawText(String.valueOf(mTotalNumber), centX + offset, centY + 2.5f * offset + bottom, mTextPaint);
    }

    private static int dp2px(Context context, float dpValue) {
        final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getApplicationContext().getResources().getDisplayMetrics());
    }
}
