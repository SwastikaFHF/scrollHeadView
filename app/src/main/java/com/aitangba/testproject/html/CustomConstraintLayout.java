package com.aitangba.testproject.html;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by XBeats on 2020/1/7
 */
public class CustomConstraintLayout extends ConstraintLayout {
    public CustomConstraintLayout(Context context) {
        super(context);
    }

    public CustomConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("Custom", "CustomConstraintLayout : width = " + CustomFrameLayout.toString(widthMeasureSpec)
                + "， height = " + CustomFrameLayout.toString(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
