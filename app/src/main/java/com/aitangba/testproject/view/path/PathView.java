package com.aitangba.testproject.view.path;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by fhf11991 on 2016/9/21.
 */

public class PathView extends View {

    private Paint mPaint;

    public PathView(Context context) {
        this(context, null);
    }

    public PathView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Path rectPath = new Path();
        rectPath.addRect(100, 100, 500, 500, Path.Direction.CW);

        Path circlePath = new Path();
        circlePath.addCircle(500, 500, 300, Path.Direction.CW);
        circlePath.addPath(rectPath);

        circlePath.addPath(rectPath);
//        circlePath.setFillType(Path.FillType.WINDING);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawPath(circlePath, mPaint);
    }
}
