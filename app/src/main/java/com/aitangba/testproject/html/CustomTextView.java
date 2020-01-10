package com.aitangba.testproject.html;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.WindowCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.WindowInsets;
import android.widget.TextView;

public class CustomTextView extends AppCompatTextView {

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.d("Custom", "CustomTextView : width = " + CustomFrameLayout.toString(widthMeasureSpec)
                + "， height = " + CustomFrameLayout.toString(heightMeasureSpec));
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            Log.d("Custom", "onApplyWindowInsets : top = " + insets.getSystemWindowInsetBottom()
                    + "， bottom = " + insets.getSystemWindowInsetBottom() + ", height = " + (insets.getSystemWindowInsetBottom() - insets.getSystemWindowInsetTop()));
        }
        return super.onApplyWindowInsets(insets);
    }
}
