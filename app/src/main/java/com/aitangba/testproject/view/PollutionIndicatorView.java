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
import android.util.TypedValue;
import android.view.View;

/**
 * Created by fhf11991 on 2018/6/13
 */

public class PollutionIndicatorView extends View {

    private static final int BRUSH_HEIGHT = 10; //dp 刻度盘高度
    private static final int TEXT_MARGIN = 8; // dp 文字刻度值到刻度盘距离
    private static final int HORIZONTAL_MARGIN = 15; //dp 刻度盘水平间距
    private static final int TEXT_SIZE = 14; // sp 文字大小
    private static final int BUBBLE_RADIUS = 12; // dp 水滴半径
    private static final int BUBBLE_HEIGHT = 28; // dp 水滴高度

    private int horizontalMargin;
    private int brushHeight;
    private int mBubbleRadius;
    private int bubbleHeight;

    private Path firstPartPath = new Path(); // 刻度盘部分
    private Path lastPartPath = new Path();
    private Paint firstPaint;
    private Paint middlePaint;
    private Paint lastPaint;

    private Paint mTextPaint; // 文字
    private Paint.FontMetricsInt mFontMetrics;

    private Path bubblePath = new Path(); // 气泡
    private RectF bubbleRectF = new RectF();
    private Paint bubblePaint;

    private int mScaleBaseline; //刻度
    private int mValueBaseline;
    private int mTextHeight;

    private int mValue = 125; //数值
    private String valueText = "轻度污染";
    private Rect valueTextRect = new Rect();

    private final int[] middleColor = {
            Color.parseColor("#71D315")
            ,Color.parseColor("#F9CF28"), Color.parseColor("#FF9624")
            , Color.parseColor("#FF3A33"), Color.parseColor("#B10064")
            ,Color.parseColor("#8F0029")};

    public PollutionIndicatorView(Context context) {
        super(context, null);
    }

    public PollutionIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PollutionIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        horizontalMargin = dp2px(context, HORIZONTAL_MARGIN);
        brushHeight = dp2px(context, BRUSH_HEIGHT);
        mBubbleRadius = dp2px(context, BUBBLE_RADIUS);
        bubbleHeight = dp2px(context, BUBBLE_HEIGHT);

        firstPaint = new Paint();
        firstPaint.setAntiAlias(true);
        firstPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        firstPaint.setColor(middleColor[0]);

        middlePaint = new Paint();
        middlePaint.setAntiAlias(true);
        middlePaint.setStyle(Paint.Style.FILL);
        middlePaint.setStrokeWidth(brushHeight * 1f);

        lastPaint = new Paint();
        lastPaint.setAntiAlias(true);
        lastPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        lastPaint.setColor(middleColor[middleColor.length - 1]);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setStrokeWidth(3);
        mTextPaint.setTextSize(sp2px(context, TEXT_SIZE));
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mFontMetrics = mTextPaint.getFontMetricsInt();
        mTextHeight = Math.abs(mFontMetrics.bottom - mFontMetrics.top);

        bubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setValue(int value, String valueText) {
        this.mValue = value;
        this.valueText = valueText;

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int textTopMargin = dp2px(getContext(), TEXT_MARGIN);

        int height = bubbleHeight + brushHeight + textTopMargin + mTextHeight;
        int width = MeasureSpec.getSize(widthMeasureSpec);

        mScaleBaseline = getPaddingTop() + bubbleHeight + brushHeight + textTopMargin + Math.abs(mFontMetrics.top);
        mValueBaseline = getPaddingTop() + mBubbleRadius + Math.abs(mFontMetrics.top) - mTextHeight / 2;

        setMeasuredDimension(width, getPaddingTop() + height + getPaddingBottom());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        final int startY = getPaddingTop() + bubbleHeight;
        final int eachDis = (w - horizontalMargin * 2) / 10;

        firstPartPath.reset();
        firstPartPath.moveTo(horizontalMargin + eachDis, startY);
        firstPartPath.lineTo(horizontalMargin + eachDis, startY + brushHeight);
        firstPartPath.arcTo(new RectF(horizontalMargin, startY, horizontalMargin + brushHeight, startY + brushHeight), 90, 180, false);

        lastPartPath.reset();
        lastPartPath.moveTo(horizontalMargin + 6 * eachDis, startY + brushHeight);
        lastPartPath.lineTo(horizontalMargin + 6 * eachDis, startY);
        lastPartPath.arcTo(new RectF(w - horizontalMargin - brushHeight, startY, w - horizontalMargin, startY + brushHeight), -90, 180, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int startY = getPaddingTop() + bubbleHeight;

        // 1.刻度盘
        canvas.drawPath(firstPartPath, firstPaint);

        final int eachDis = (getMeasuredWidth() - horizontalMargin * 2) / 10;
        final int middlePartY = startY + brushHeight / 2;

        middlePaint.setColor(middleColor[1]);
        canvas.drawLine(horizontalMargin + eachDis, middlePartY, horizontalMargin + 2 * eachDis, middlePartY, middlePaint);

        middlePaint.setColor(middleColor[2]);
        canvas.drawLine(horizontalMargin + 2 * eachDis, middlePartY, horizontalMargin + 3 * eachDis, middlePartY, middlePaint);

        middlePaint.setColor(middleColor[3]);
        canvas.drawLine(horizontalMargin + 3 * eachDis, middlePartY, horizontalMargin + 4 * eachDis, middlePartY, middlePaint);

        middlePaint.setColor(middleColor[4]);
        canvas.drawLine(horizontalMargin + 4 * eachDis, middlePartY, horizontalMargin + 6 * eachDis, middlePartY, middlePaint);

        canvas.drawPath(lastPartPath, lastPaint);


        // 2.刻度
        canvas.drawText("0", horizontalMargin, mScaleBaseline, mTextPaint);
        canvas.drawText("50", horizontalMargin + eachDis, mScaleBaseline, mTextPaint);
        canvas.drawText("100", horizontalMargin + 2 * eachDis, mScaleBaseline, mTextPaint);
        canvas.drawText("150", horizontalMargin + 3 * eachDis, mScaleBaseline, mTextPaint);
        canvas.drawText("200", horizontalMargin + 4 * eachDis, mScaleBaseline, mTextPaint);
        canvas.drawText("300", horizontalMargin + 6 * eachDis, mScaleBaseline, mTextPaint);
        canvas.drawText("500", getMeasuredWidth() - horizontalMargin, mScaleBaseline, mTextPaint);

        // 3.气泡
        int value = Math.min(500, Math.max(0, mValue)); // 0~500
        int bubbleX = (int) (value / 50f * eachDis + horizontalMargin);
        int curveControlY = startY - mBubbleRadius * 7 / 12; // 贝塞尔曲线控制点Y坐标
        bubbleRectF.left = bubbleX - mBubbleRadius;
        bubbleRectF.top = getPaddingTop();
        bubbleRectF.right = bubbleX + mBubbleRadius;
        bubbleRectF.bottom = getPaddingTop() + mBubbleRadius * 2;
        bubblePath.moveTo(bubbleX + mBubbleRadius, getPaddingTop() + mBubbleRadius);
        bubblePath.quadTo(bubbleX + mBubbleRadius, curveControlY, bubbleX, startY); //(x1,y1) 为控制点，(x2,y2)为结束点。
        bubblePath.quadTo(bubbleX - mBubbleRadius, curveControlY, bubbleX - mBubbleRadius, getPaddingTop() + mBubbleRadius);
        bubblePath.arcTo(bubbleRectF, 180, 180, false);
        bubblePaint.setColor(getBubbleColor(value));
        canvas.drawPath(bubblePath, bubblePaint);

        // 4.数值
        canvas.drawText(String.valueOf(mValue), bubbleX, mValueBaseline, mTextPaint);

        // 5.数值对应的信息
        mTextPaint.getTextBounds(valueText, 0, valueText.length(), valueTextRect);
        int valueTextPointX = bubbleX + mBubbleRadius + 20 + valueTextRect.width() / 2;
        canvas.drawText(valueText, valueTextPointX, mValueBaseline, mTextPaint);
    }

    private int getBubbleColor(int value) {
        if(value < 50) {
            return middleColor[0];
        } else if(value < 100) {
            return middleColor[1];
        } else if(value < 150) {
            return middleColor[2];
        } else if(value < 200) {
            return middleColor[3];
        } else if(value < 300) {
            return middleColor[4];
        } else {
            return middleColor[5];
        }
    }

    private static int dp2px(Context context, float dpValue) {
        final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getApplicationContext().getResources().getDisplayMetrics());
    }
}
