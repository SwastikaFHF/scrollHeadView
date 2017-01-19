package com.aitangba.testproject.irregularview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by fhf11991 on 2017/1/16.
 */

public class IrregularView extends View {

    private Path p = new Path();

    private Region re = new Region();
    private Path mExternalCirclePath;
    private Path mInsideCirclePath;
    private Paint mDefaultPaint;
    private Path mLeftPath;
    private Path mTopPath;
    private Path mRightPath;
    private Path mBottomPath;

    public IrregularView(Context context) {
        this(context, null);
    }

    public IrregularView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IrregularView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mDefaultPaint = new Paint();
        mDefaultPaint.setAntiAlias(true);
        mDefaultPaint.setStyle(Paint.Style.FILL);
        mDefaultPaint.setColor(Color.RED);

        mExternalCirclePath = new Path();

        mInsideCirclePath = new Path();

        mLeftPath = new Path();
        mTopPath = new Path();
        mRightPath = new Path();
        mBottomPath = new Path();

        RectF r = new RectF();
        //计算控制点的边界
        p.computeBounds(r, true);
        //设置区域路径和剪辑描述的区域
        re.setPath(p, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
    }

    private float externalRadius = 400;
    private float insideRadius = 200;
    private float offset = 20;

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        Log.d("", "w = " + w + "   olw = " + oldW);
        final float centPointX = w / 2;
        final float centPointY = h / 2;
        mExternalCirclePath.addCircle(centPointX, centPointY, externalRadius, Path.Direction.CW);
        mExternalCirclePath.addCircle(centPointX, centPointY, insideRadius, Path.Direction.CCW);
        mExternalCirclePath.setFillType(Path.FillType.WINDING);

        mLeftPath.moveTo(centPointX - offset, centPointY);
        mLeftPath.lineTo(centPointX - externalRadius, centPointY + (externalRadius - offset));
        mLeftPath.lineTo(centPointX - externalRadius, centPointY - (externalRadius - offset));
        mLeftPath.close();

        mTopPath.moveTo(centPointX, centPointY - offset);
        mTopPath.lineTo(centPointX - (externalRadius - offset), centPointY - externalRadius);
        mTopPath.lineTo(centPointX + (externalRadius - offset), centPointY - externalRadius);
        mTopPath.close();

        mRightPath.moveTo(centPointX + offset, centPointY);
        mRightPath.lineTo(centPointX + externalRadius, centPointY - (externalRadius - offset));
        mRightPath.lineTo(centPointX + externalRadius, centPointY + (externalRadius - offset));
        mRightPath.close();

        mBottomPath.moveTo(centPointX, centPointY + offset);
        mBottomPath.lineTo(centPointX + (externalRadius - offset), centPointY + externalRadius);
        mBottomPath.lineTo(centPointX - (externalRadius - offset), centPointY + externalRadius);
        mBottomPath.close();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.clipPath(mExternalCirclePath);
        canvas.drawPath(mLeftPath, mDefaultPaint);
        canvas.drawPath(mTopPath, mDefaultPaint);
        canvas.drawPath(mRightPath, mDefaultPaint);
        canvas.drawPath(mBottomPath, mDefaultPaint);

//        canvas.clipPath(mExternalCirclePath);
//        canvas.drawColor(Color.BLUE);
    }
}
