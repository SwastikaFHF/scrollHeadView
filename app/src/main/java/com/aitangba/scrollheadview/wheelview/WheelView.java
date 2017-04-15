package com.aitangba.scrollheadview.wheelview;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by XBeats on 2016/9/26.
 */

public class WheelView extends LinearLayout {
    public WheelView(Context context) {
        super(context);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    float mLastMotionY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = ev.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                float motionY = ev.getY();
                requestLayout();
                break;
            default:break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
