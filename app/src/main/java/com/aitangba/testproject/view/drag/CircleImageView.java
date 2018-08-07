package com.aitangba.testproject.view.drag;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by fhf11991 on 2018/8/7
 */
public class CircleImageView extends AppCompatImageView {

    private Paint mForegroundPaint;
    private Path mPath = new Path();

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mForegroundPaint = new Paint();
        mForegroundPaint.setStrokeWidth(0f);
        mForegroundPaint.setAntiAlias(true);
        mForegroundPaint.setColor(Color.WHITE);
        mForegroundPaint.setStyle(Paint.Style.FILL);

    }

    public void setForegroundColor(@ColorInt int foregroundColor) {
        mForegroundPaint.setColor(foregroundColor);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);

        int radius = Math.max(width, height) / 2;
        mPath.reset();
        mPath.moveTo(0, 0);
        mPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        mPath.addCircle(width / 2, height / 2, radius, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.drawPath(mPath, mForegroundPaint);
        canvas.restore();
    }
}
