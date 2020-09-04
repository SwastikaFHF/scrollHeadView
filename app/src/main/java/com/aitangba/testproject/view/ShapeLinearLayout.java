package com.aitangba.testproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by fhf11991 on 2018/7/11
 */
public class ShapeLinearLayout extends LinearLayout {

    private Path mPath = new Path();
    private RectF mRect = new RectF();
    private Paint mMaskPaint = new Paint();

    public ShapeLinearLayout(Context context) {
        this(context, null);
    }

    public ShapeLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mMaskPaint.setAntiAlias(true);
        mMaskPaint.setStyle(Paint.Style.FILL);
        mMaskPaint.setColor(ContextCompat.getColor(context, android.R.color.white));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mRect.set(0, 0, w, h);
        mPath.reset();
        mPath.addRoundRect(mRect ,50 , 50 , Path.Direction.CW);
        mPath.setFillType(Path.FillType.INVERSE_WINDING);

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        super.dispatchDraw(canvas);
        canvas.drawPath(mPath, mMaskPaint);
        canvas.restore();
    }
}
