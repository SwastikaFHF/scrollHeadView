package com.aitangba.testproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by fhf11991 on 2018/6/13
 */

public class PollutionIndicatorView extends View {

    private static final int BRUSH_HEIGHT = 30; //px
    private static final int MARGIN_SPACE = 100; //px
    private static final int TEXT_MARGIN = 20; // px

    private Path firstPartPath = new Path(); // 刻度盘
    private Path lastPartPath = new Path();
    private Paint firstPaint;
    private Paint middlePaint;
    private Paint lastPaint;

    private int bubbleHeight = 100; // 刻度盘距离

    private Paint mTextPaint; // 文字
    private Paint.FontMetricsInt mFontMetrics;

    private Path bubblePath = new Path(); // 气泡
    private RectF bubbleRectF = new RectF();
    private Paint bubblePaint;
    private int mBubbleRadius = 40;

    private int mScaleBaseline; //刻度
    private int mValueBaseline; //
    private int mTextHeight;

    private int value = 125; //数值
    private String valueText = "轻度污染";
    private Rect valueTextRect = new Rect();

    private final int[] middleColor = {
            Color.parseColor("#F9CF28"), Color.parseColor("#FF9624")
            , Color.parseColor("#FF3A33"), Color.parseColor("#B10064")};

    public PollutionIndicatorView(Context context) {
        super(context, null);
    }

    public PollutionIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PollutionIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        firstPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        firstPaint.setColor(Color.parseColor("#71D315"));

        middlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        middlePaint.setStrokeWidth(BRUSH_HEIGHT);

        lastPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lastPaint.setColor(Color.parseColor("#8F0029"));

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setStrokeWidth(3);
        mTextPaint.setTextSize(40);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mFontMetrics = mTextPaint.getFontMetricsInt();
        mTextHeight = Math.abs(mFontMetrics.bottom - mFontMetrics.top);

        bubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bubblePaint.setColor(Color.parseColor("#FF9624"));
    }

    public void setValue(int value, String valueText) {
        this.value = value;
        this.valueText = valueText;

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = bubbleHeight + BRUSH_HEIGHT + TEXT_MARGIN + mTextHeight;
        int width = MeasureSpec.getSize(widthMeasureSpec);

        mScaleBaseline = getPaddingTop() + bubbleHeight + BRUSH_HEIGHT + TEXT_MARGIN + Math.abs(mFontMetrics.top);
        mValueBaseline = getPaddingTop() + mBubbleRadius + Math.abs(mFontMetrics.top) - mTextHeight / 2;

        setMeasuredDimension(width, getPaddingTop() + height + getPaddingBottom());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        final int startY = getPaddingTop() + bubbleHeight;
        final int eachDis = (w - MARGIN_SPACE * 2) / 10;

        firstPartPath.reset();
        firstPartPath.moveTo(MARGIN_SPACE + eachDis, startY);
        firstPartPath.lineTo(MARGIN_SPACE + eachDis, startY + BRUSH_HEIGHT);
        firstPartPath.arcTo(new RectF(MARGIN_SPACE, startY, MARGIN_SPACE + BRUSH_HEIGHT, startY + BRUSH_HEIGHT), 90, 180, false);

        lastPartPath.reset();
        lastPartPath.moveTo(MARGIN_SPACE + 6 * eachDis, startY + BRUSH_HEIGHT);
        lastPartPath.lineTo(MARGIN_SPACE + 6 * eachDis, startY);
        lastPartPath.arcTo(new RectF(w - MARGIN_SPACE - BRUSH_HEIGHT, startY, w - MARGIN_SPACE, startY + BRUSH_HEIGHT), -90, 180, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int startY = getPaddingTop() + bubbleHeight;

        // 1.刻度盘
        canvas.drawPath(firstPartPath, firstPaint);

        final int eachDis = (getMeasuredWidth() - MARGIN_SPACE * 2) / 10;
        final int middlePartY = startY + BRUSH_HEIGHT / 2;

        middlePaint.setColor(middleColor[0]);
        canvas.drawLine(MARGIN_SPACE + eachDis, middlePartY, MARGIN_SPACE + 2 * eachDis, middlePartY, middlePaint);

        middlePaint.setColor(middleColor[1]);
        canvas.drawLine(MARGIN_SPACE + 2 * eachDis, middlePartY, MARGIN_SPACE + 3 * eachDis, middlePartY, middlePaint);

        middlePaint.setColor(middleColor[2]);
        canvas.drawLine(MARGIN_SPACE + 3 * eachDis, middlePartY, MARGIN_SPACE + 4 * eachDis, middlePartY, middlePaint);

        middlePaint.setColor(middleColor[3]);
        canvas.drawLine(MARGIN_SPACE + 4 * eachDis, middlePartY, MARGIN_SPACE + 6 * eachDis, middlePartY, middlePaint);

        canvas.drawPath(lastPartPath, lastPaint);


        // 2.刻度
        canvas.drawText("0", MARGIN_SPACE, mScaleBaseline, mTextPaint);
        canvas.drawText("50", MARGIN_SPACE + eachDis, mScaleBaseline, mTextPaint);
        canvas.drawText("100", MARGIN_SPACE + 2 * eachDis, mScaleBaseline, mTextPaint);
        canvas.drawText("150", MARGIN_SPACE + 3 * eachDis, mScaleBaseline, mTextPaint);
        canvas.drawText("200", MARGIN_SPACE + 4 * eachDis, mScaleBaseline, mTextPaint);
        canvas.drawText("300", MARGIN_SPACE + 6 * eachDis, mScaleBaseline, mTextPaint);
        canvas.drawText("500", getMeasuredWidth() - MARGIN_SPACE, mScaleBaseline, mTextPaint);

        // 3.气泡
        int bubbleX = (int) (value / 50f * eachDis + MARGIN_SPACE);
        bubbleRectF.left = bubbleX - mBubbleRadius;
        bubbleRectF.top = getPaddingTop();
        bubbleRectF.right = bubbleX + mBubbleRadius;
        bubbleRectF.bottom = getPaddingTop() + mBubbleRadius * 2;
        bubblePath.moveTo(bubbleX + mBubbleRadius, getPaddingTop() + mBubbleRadius);
        bubblePath.quadTo(bubbleX, startY + 50, bubbleX - mBubbleRadius, getPaddingTop() + mBubbleRadius);
        bubblePath.arcTo(bubbleRectF, 180, 180, false);
        canvas.drawPath(bubblePath, bubblePaint);

        // 4.数值
        canvas.drawText(String.valueOf(value), bubbleX, mValueBaseline, mTextPaint);

        // 5.数值对应的信息
        mTextPaint.getTextBounds(valueText, 0, valueText.length(), valueTextRect);
        int valueTextPointX = bubbleX + mBubbleRadius + 20 + valueTextRect.width() / 2;
        canvas.drawText(valueText, valueTextPointX, mValueBaseline, mTextPaint);
    }
}
