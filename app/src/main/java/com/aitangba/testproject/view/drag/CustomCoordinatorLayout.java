package com.aitangba.testproject.view.drag;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by fhf11991 on 2018/8/10
 */
public class CustomCoordinatorLayout extends CoordinatorLayout {

    private static final String TAG = "CustomCoordinatorLayout";

    public CustomCoordinatorLayout(Context context) {
        super(context);
    }

    public CustomCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onStopNestedScroll(View target) {
        super.onStopNestedScroll(target);
        Log.d(TAG, "onStopNestedScroll- -------");
    }
}
