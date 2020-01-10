package com.aitangba.testproject.html;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

/**
 * Created by XBeats on 2020/1/7
 */
public class CustomFrameLayout extends FrameLayout {
    public CustomFrameLayout(@NonNull Context context) {
        super(context);
    }

    public CustomFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("Custom", "CustomFrameLayout : width = " + toString(widthMeasureSpec)
                + "， height = " + toString(heightMeasureSpec));
        if(getParent() instanceof ConstraintLayout) {
            ConstraintLayout parent = (ConstraintLayout) getParent();
            Log.d("Custom", "CustomFrameLayout ConstraintWidget : width = " + parent.getViewWidget(this).getWidth()
                    + "， height = " + parent.getViewWidget(this).getHeight());
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public static String toString(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        StringBuilder sb = new StringBuilder("MeasureSpec: ");

        if (mode == MeasureSpec.UNSPECIFIED)
            sb.append("UNSPECIFIED ");
        else if (mode == MeasureSpec.EXACTLY)
            sb.append("EXACTLY ");
        else if (mode == MeasureSpec.AT_MOST)
            sb.append("AT_MOST ");
        else
            sb.append(mode).append(" ");

        sb.append(size);
        return sb.toString();
    }

}
