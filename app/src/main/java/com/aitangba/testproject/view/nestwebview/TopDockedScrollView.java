package com.aitangba.testproject.view.nestwebview;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by XBeats on 2020/8/6
 */
public class TopDockedScrollView extends NestedScrollView {

    private View mTopDockedView;

    public TopDockedScrollView(@NonNull Context context) {
        super(context);
    }

    public TopDockedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TopDockedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTopDockedView(View topDockedView) {
        mTopDockedView = topDockedView;
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        if(mTopDockedView == null) {
            super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
            return;
        }

        int oldScrollY = this.getScrollY();
        if(oldScrollY + dyUnconsumed > mTopDockedView.getTop()) {
            this.scrollBy(0, mTopDockedView.getTop() - oldScrollY);
            int myConsumed = this.getScrollY() - oldScrollY;
            int myUnconsumed = dyUnconsumed - myConsumed;
            this.dispatchNestedScroll(0, myConsumed, 0, myUnconsumed, (int[])null, type);
        } else {
            super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        }

    }
}
