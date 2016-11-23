package com.aitangba.testproject.loadingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by fhf11991 on 2016/11/23.
 */

public class LoadingView extends View {

    private Path mTopPath;
    private Paint mTopPaint;

    private Path mLeftPath;
    private Paint mLeftPaint;

    private Path mRightPath;
    private Paint mRightPaint;

    private Paint mPointPaint;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private final int length = 200;
    private final int CENT_POINT_X = 400;
    private final int CENT_POINT_Y = 400;
    private final float sin30 = (float) Math.sin(30.0 * Math.PI / 180.0);
    private final float cos30 = (float) Math.cos(30.0 * Math.PI / 180.0);

    private void init() {

        // init top
        mTopPaint = new Paint();
        mTopPaint.setColor(Color.parseColor("#FDFDE3"));

        mTopPath = new Path();
        mTopPath.moveTo(0, 0);
        mTopPath.lineTo(-length * sin30, -length * cos30);
        mTopPath.lineTo(-length * sin30 + length, -length * cos30);
        mTopPath.lineTo(length, 0);
        mTopPath.close();

        // init left
        mLeftPaint = new Paint();
        mLeftPaint.setColor(Color.parseColor("#EEDC70"));

        mLeftPath = new Path();
        mLeftPath.moveTo(0, 0);
        mLeftPath.lineTo(length * sin30, -length * cos30);
        mLeftPath.lineTo(length * sin30 + length, -length * cos30);
        mLeftPath.lineTo(length, 0);
        mLeftPath.close();

        // init left
        mRightPaint = new Paint();
        mRightPaint.setColor(Color.parseColor("#FAECA4"));

        mRightPath = new Path();
        mRightPath.moveTo(0, 0);
        mRightPath.lineTo(-length * sin30, -length * cos30);
        mRightPath.lineTo(-length * sin30 + length, -length * cos30);
        mRightPath.lineTo(length, 0);
        mRightPath.close();

        mPointPaint = new Paint();
        mPointPaint.setColor(Color.RED);
        mPointPaint.setStrokeWidth(20);
        mPointPaint.setAntiAlias(true);
        mPointPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int boomIndex = canvas.save();

        //first
        int left = CENT_POINT_X;
        int top = CENT_POINT_Y;
        drawDiamond(canvas, left, top);

        canvas.restoreToCount(boomIndex);
        boomIndex = canvas.save();

        //second
        left = CENT_POINT_X + (int)(length * cos30);
        top = CENT_POINT_Y + (int)(length * sin30);
        drawDiamond(canvas, left, top);

        canvas.restoreToCount(boomIndex);
        boomIndex = canvas.save();

        //third
        left = CENT_POINT_X;
        top = CENT_POINT_Y + (int)(length * sin30) * 2;
        drawDiamond(canvas, left, top);

        canvas.restoreToCount(boomIndex);
        boomIndex = canvas.save();

        //fourth
        left = CENT_POINT_X + (int)(length * cos30);
        top = CENT_POINT_Y + (int)(length * sin30) * 3;
        drawDiamond(canvas, left, top);

        canvas.restoreToCount(boomIndex);
        boomIndex = canvas.save();
    }

    private void drawDiamond(Canvas canvas, int left, int top) {
        canvas.translate(left, top);
        int secondIndex = canvas.save();

        canvas.rotate(-30);
        canvas.drawPath(mTopPath, mTopPaint);

        canvas.restoreToCount(secondIndex);
        secondIndex = canvas.save();

        canvas.translate(0, length);
        canvas.rotate(-90);
        canvas.drawPath(mLeftPath, mLeftPaint);

        canvas.restoreToCount(secondIndex);

        canvas.rotate(90);
        canvas.drawPath(mRightPath, mRightPaint);


    }
}
