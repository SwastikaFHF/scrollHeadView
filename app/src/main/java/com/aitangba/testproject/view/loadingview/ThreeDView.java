package com.aitangba.testproject.view.loadingview;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by fhf11991 on 2016/12/1.
 */

public class ThreeDView extends View {

    private Camera mCamera;
    private Paint mTopPaint;
    private Path mPath;
    private Matrix matrix = new Matrix();

    public ThreeDView(Context context) {
        this(context, null);
    }

    public ThreeDView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThreeDView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        mCamera = new Camera();

        mTopPaint = new Paint();

        mTopPaint.setStrokeWidth(20);


        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(200, 0);
        mPath.lineTo(200, 200);
        mPath.lineTo(0, 200);
        mPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(100, 100);


        mTopPaint.setColor(Color.BLUE);
        canvas.drawPath(mPath, mTopPaint);

//        canvas.translate(210, 0);

//        mCamera.translate(110, 0, 0);
        mCamera.setLocation(-10, 10, -10);
//        mCamera.rotateY(10);
//        mCamera.rotate(0, 30 ,-30);
        mCamera.getMatrix(matrix);

        canvas.concat(matrix);
        mTopPaint.setColor(Color.RED);
        canvas.drawPath(mPath, mTopPaint);
    }
}
