package com.aitangba.testproject.tracktask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class IrregularView extends View {

    private Path mLeftPath = new Path();
    private Paint mLeftPaint;
    private Region mLeftRegion = new Region();

    private Path mRightPath = new Path();
    private Paint mRightPaint;
    private Region mRightRegion = new Region();

    private RectF mRectF = new RectF(); // 描述区域

    private int mCheckedDirection = LEFT;

    public IrregularView(Context context) {
        this(context, null);
    }

    public IrregularView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IrregularView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLeftPaint = new Paint();
        mLeftPaint.setStyle(Paint.Style.FILL);
        mLeftPaint.setColor(ContextCompat.getColor(context, android.R.color.white));

        mRightPaint = new Paint();
        mRightPaint.setStyle(Paint.Style.FILL);
        mRightPaint.setColor(ContextCompat.getColor(context, android.R.color.black));
    }

    public void setCheckedDirection(@CheckedDirection int checkedDirection) {
        mCheckedDirection = checkedDirection;
        resetPath(getMeasuredWidth(), getMeasuredHeight());
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        resetPath(w, h);
    }

    private void resetPath(int w, int h) {
        mLeftPath.reset();
        mRightPath.reset();
        if (w != 0 && h != 0) {
            double distance = h * Math.tan(Math.PI / 12);

            if(mCheckedDirection == LEFT) {
                mLeftPath.moveTo(0, 0);
                mLeftPath.lineTo(w / 2 + (int) distance / 2, 0);
                mLeftPath.lineTo(w / 2 - (int) distance / 2, h);
                mLeftPath.lineTo(0, h);
                mLeftPath.close();

                mRightPath.moveTo(w / 2 + (int) distance / 2, 0);
                mRightPath.lineTo(w, 0);
                mRightPath.lineTo(w, h);
                mRightPath.lineTo(w / 2 - (int) distance / 2, h);
                mRightPath.close();
            } else if(mCheckedDirection == RIGHT){
                mLeftPath.moveTo(0, 0);
                mLeftPath.lineTo(w / 2 - (int) distance / 2, 0);
                mLeftPath.lineTo(w / 2 + (int) distance / 2, h);
                mLeftPath.lineTo(0, h);
                mLeftPath.close();

                mRightPath.moveTo(w / 2 - (int) distance / 2, 0);
                mRightPath.lineTo(w, 0);
                mRightPath.lineTo(w, h);
                mRightPath.lineTo(w / 2 + (int) distance / 2, h);
                mRightPath.close();
            } else {
                return;
            }

            mLeftPath.computeBounds(mRectF, true);
            mLeftRegion.setPath(mLeftPath, new Region((int) mRectF.left, (int) mRectF.top, (int) mRectF.right, (int) mRectF.bottom));


            mRightPath.computeBounds(mRectF, true);
            mRightRegion.setPath(mRightPath, new Region((int) mRectF.left, (int) mRectF.top, (int) mRectF.right, (int) mRectF.bottom));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mLeftPath, mLeftPaint);
        canvas.drawPath(mRightPath, mRightPaint);
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LEFT, RIGHT})
    public @interface CheckedDirection {}

    public static final int LEFT = 1;
    public static final int RIGHT = 2;
}
