package com.aitangba.testproject.tracktask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

public class IrregularView extends View {

    private Path mPath = new Path();
    private Paint mPaint;

    public IrregularView(Context context) {
        this(context, null);
    }

    public IrregularView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IrregularView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(ContextCompat.getColor(context, android.R.color.white));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPath.moveTo(0, 0);
        mPath.lineTo(w/2, 0);
        mPath.lineTo(w/2, h);
        mPath.lineTo(0, h);
        mPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);
    }
}
