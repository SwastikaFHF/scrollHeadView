package com.aitangba.testproject.cornerrectangle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by fhf11991 on 2016/7/5.
 */
public class CornerTextView extends TextView{

    private int mBackgroundColor = Color.parseColor("#F36E65");
    private int paddingHorizon = 30;

    @Override
    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
    }

    public CornerTextView(Context context) {
        this(context, null);
    }

    public CornerTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CornerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setPadding(paddingHorizon, 0, paddingHorizon, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        final int radius = paddingHorizon;

        //画圆角矩形
        Paint p = new Paint();
        p.setColor(mBackgroundColor);// 设置红色
        p.setStyle(Paint.Style.FILL);//充满
        p.setAntiAlias(true);// 设置画笔的锯齿效果

        RectF oval3 = new RectF(0, 0, width, height);// 设置个新的长方形
        canvas.drawRoundRect(oval3, radius, radius, p);//第二个参数是x半径，第三个参数是y半径

        super.onDraw(canvas);
    }
}
