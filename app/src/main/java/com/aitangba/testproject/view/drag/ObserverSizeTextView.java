package com.aitangba.testproject.view.drag;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by fhf11991 on 2018/8/8
 */
public class ObserverSizeTextView extends AppCompatTextView {

    public ObserverSizeTextView(Context context) {
        super(context);
    }

    public ObserverSizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObserverSizeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(mOnSizeChangedListener != null) {
            mOnSizeChangedListener.onSizeChanged(this, w, h, oldw, oldh);
        }
    }

    private OnSizeChangedListener mOnSizeChangedListener;

    public void setOnSizeChangedListener(OnSizeChangedListener onSizeChangedListener) {
        mOnSizeChangedListener = onSizeChangedListener;
    }

    public interface OnSizeChangedListener {
        void onSizeChanged(ObserverSizeTextView textView, int w, int h, int oldw, int oldh);
    }
}
