package com.aitangba.testproject.irregularview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by fhf11991 on 2017/1/16.
 */

public class RemoteControlMenu extends View {

    private static final int AREA_BLANK = -1;
    private static final int AREA_CENTER = 0;
    private static final int AREA_UP = 1;
    private static final int AREA_RIGHT = 2;
    private static final int AREA_DOWN = 3;
    private static final int AREA_LEFT = 4;

    private static final int COLOR_DEFAULT = 0xFF4E5268;
    private static final int COLOR_TOUCHED = 0xFFDF9C81;

    private CustomPath upPath, downPath, leftPath, rightPath, centerPath;
    private Region up, down, left, right, center;
    private Paint mPaint;
    private CustomPath[] mPaths;
    private MenuListener mListener;

    private int touchFlag = -1;
    private int currentFlag = -1;

    public RemoteControlMenu(Context context) {
        this(context, null);
    }

    public RemoteControlMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RemoteControlMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        upPath = new CustomPath(AREA_UP);
        downPath = new CustomPath(AREA_DOWN);
        leftPath = new CustomPath(AREA_LEFT);
        rightPath = new CustomPath(AREA_RIGHT);
        centerPath = new CustomPath(AREA_CENTER);

        up = new Region();
        down = new Region();
        left = new Region();
        right = new Region();
        center = new Region();

        mPaint = new Paint();
        mPaint.setColor(COLOR_DEFAULT);
        mPaint.setAntiAlias(true);

        mPaths = new CustomPath[]{centerPath, leftPath, upPath, rightPath, downPath};
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // 注意这个区域的大小
        Region globalRegion = new Region(-w / 2, -h / 2, w / 2, h / 2);
        int minWidth = w > h ? h : w;
        minWidth *= 0.8;

        int br = minWidth / 2;
        RectF bigCircle = new RectF(-br, -br, br, br);

        int sr = minWidth / 4;
        RectF smallCircle = new RectF(-sr, -sr, sr, sr);

        float bigSweepAngle = 84;
        float smallSweepAngle = -80;

        // 根据视图大小，初始化 Path 和 Region
        centerPath.addCircle(0, 0, 0.2f * minWidth, Path.Direction.CW);
        center.setPath(centerPath, globalRegion);

        rightPath.addArc(bigCircle, -40, bigSweepAngle);
        rightPath.arcTo(smallCircle, 40, smallSweepAngle);
        rightPath.close();
        right.setPath(rightPath, globalRegion);

        downPath.addArc(bigCircle, 50, bigSweepAngle);
        downPath.arcTo(smallCircle, 130, smallSweepAngle);
        downPath.close();
        down.setPath(downPath, globalRegion);

        leftPath.addArc(bigCircle, 140, bigSweepAngle);
        leftPath.arcTo(smallCircle, 220, smallSweepAngle);
        leftPath.close();
        left.setPath(leftPath, globalRegion);

        upPath.addArc(bigCircle, 230, bigSweepAngle);
        upPath.arcTo(smallCircle, 310, smallSweepAngle);
        upPath.close();
        up.setPath(upPath, globalRegion);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touchFlag = getTouchedPath(x, y);
                currentFlag = touchFlag;
                break;
            case MotionEvent.ACTION_MOVE:
                currentFlag = getTouchedPath(x, y);
                if(currentFlag != touchFlag) { //cancel the TouchEvent
                    touchFlag = currentFlag = AREA_BLANK;
                    invalidate();
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                currentFlag = getTouchedPath(x, y);
                // 如果手指按下区域和抬起区域相同且不为空，则判断点击事件
                if (currentFlag == touchFlag && currentFlag != -1 && mListener != null) {
                    if (currentFlag == AREA_CENTER) {
                        mListener.onCenterCliched();
                    } else if (currentFlag == AREA_UP) {
                        mListener.onUpCliched();
                    } else if (currentFlag == AREA_RIGHT) {
                        mListener.onRightCliched();
                    } else if (currentFlag == AREA_DOWN) {
                        mListener.onDownCliched();
                    } else if (currentFlag == AREA_LEFT) {
                        mListener.onLeftCliched();
                    }
                }
                touchFlag = currentFlag = AREA_BLANK;
                break;
            case MotionEvent.ACTION_CANCEL:
                touchFlag = currentFlag = AREA_BLANK;
                break;
        }

        invalidate();
        return true;
    }

    /**
     * 获取当前触摸点在哪个区域
     */
    private int getTouchedPath(float xf, float yf) {
        int x = (int) (xf - getMeasuredWidth() / 2);
        int y = (int) (yf - getMeasuredHeight() / 2);
        if (center.contains(x, y)) {
            return AREA_CENTER;
        } else if (up.contains(x, y)) {
            return AREA_UP;
        } else if (right.contains(x, y)) {
            return AREA_RIGHT;
        } else if (down.contains(x, y)) {
            return AREA_DOWN;
        } else if (left.contains(x, y)) {
            return AREA_LEFT;
        }
        return AREA_BLANK;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(getMeasuredWidth() / 2, getMeasuredHeight() / 2);

        for(CustomPath path : mPaths) {
            if(path.areaType == currentFlag) {
                mPaint.setColor(COLOR_TOUCHED);
            } else {
                mPaint.setColor(COLOR_DEFAULT);
            }
            canvas.drawPath(path, mPaint);
        }
    }

    public void setListener(MenuListener listener) {
        mListener = listener;
    }

    // 点击事件监听器
    public interface MenuListener {
        void onCenterCliched();

        void onUpCliched();

        void onRightCliched();

        void onDownCliched();

        void onLeftCliched();
    }

    private static class CustomPath extends Path {

        public int areaType;

        public CustomPath(int areaType) {
            this.areaType = areaType;
        }
    }
}
