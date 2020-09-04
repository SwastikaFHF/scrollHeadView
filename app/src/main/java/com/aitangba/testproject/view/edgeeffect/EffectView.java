package com.aitangba.testproject.view.edgeeffect;

import android.content.Context;
import android.graphics.Canvas;
import androidx.core.view.ViewCompat;
import androidx.core.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by fhf11991 on 2016/9/10.
 */

public class EffectView extends View {

    public EffectView(Context context) {
        super(context);
        initView();
    }

    public EffectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public EffectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private EdgeEffectCompat mLeftEdge;
    private EdgeEffectCompat mRightEdge;

    private void initView() {
        mLeftEdge = new EdgeEffectCompat(getContext());
        mRightEdge = new EdgeEffectCompat(getContext());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean needsInvalidate = false;
        if(!mLeftEdge.isFinished()) {
            final int height = getMeasuredHeight();
            final int width = getMeasuredWidth();
            final int restoreCount = canvas.save();
            mLeftEdge.setSize(height, width);
            canvas.translate(0, height);
            canvas.rotate(270);
            needsInvalidate |= mLeftEdge.draw(canvas);
            canvas.restoreToCount(restoreCount);
        }

        if(needsInvalidate) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
    private float mLastPointX;  //记录手势在屏幕上的X轴坐标
    private float mDistance;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        boolean needsInvalidate = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastPointX = event.getRawX();
                mDistance = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                final float curPointX = event.getRawX();
                final float distanceX = curPointX - mLastPointX;
                mDistance += distanceX;

                final float curPointY = event.getY();
                needsInvalidate = mLeftEdge.onPull(Math.abs(mDistance) / width, 1 - curPointY / height);
                break;
            case MotionEvent.ACTION_UP:
                needsInvalidate = mLeftEdge.onRelease();
                break;
            default:break;
        }

        if(needsInvalidate) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
        return true;
    }
}
