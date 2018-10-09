package com.aitangba.testproject.view.slideback.slidingpanelayout;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.aitangba.testproject.view.slideback.Utils;

/**
 * Created by fhf11991 on 2016/7/11.
 */
public class BaseSlideBackActivity extends AppCompatActivity {

    private SlideBackLayout mSlideBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSlideBackFinish();
    }

    /**
     * 初始化滑动返回
     */
    private void initSlideBackFinish() {
        if (isSupportSlideBack()) {
            getSlideBackLayout();
        }
    }

    public SlideBackLayout getSlideBackLayout() {
        if(mSlideBackLayout == null ) {
            mSlideBackLayout = new SlideBackLayout(this);
            mSlideBackLayout.attachViewToActivity(this);
            mSlideBackLayout.setSlidingAvailable(true);
            mSlideBackLayout.setTouchMode(SlideBackLayout.TOUCH_MODE_MARGIN);
            mSlideBackLayout.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
                @Override
                public void onPanelSlide(@NonNull View panel, float slideOffset) {
                    Utils.convertActivityToTranslucent(BaseSlideBackActivity.this);
                    getWindow().getDecorView().setBackgroundDrawable(null);
                }

                @Override
                public void onPanelOpened(@NonNull View panel) {

                }

                @Override
                public void onPanelClosed(@NonNull View panel) {

                }
            });
        }
        return mSlideBackLayout;
    }

    /**
     * 是否支持滑动返回
     *
     * @return
     */
    protected boolean isSupportSlideBack() {
        return true;
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d("SlideBack", "onStop------");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("SlideBack", "onDestroy----------");
    }
}
