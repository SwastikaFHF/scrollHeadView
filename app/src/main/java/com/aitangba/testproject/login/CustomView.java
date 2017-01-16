package com.aitangba.testproject.login;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by fhf11991 on 2017/1/11.
 */

public class CustomView extends FrameLayout {

    private Callback mCallback;
    private boolean mIsComplete;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if(mCallback != null && !mIsComplete) {
            mIsComplete = mCallback.onDrawEnd(this, "");
        }
    }
}
