package com.aitangba.testproject.horizonscrollview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.aitangba.testproject.R;
import com.aitangba.testproject.slideback.slidinglayout.BaseActivity;

/**
 * Created by fhf11991 on 2016/9/1.
 */

public class HorizonScrollAdActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizon_scroll);

        SlideViewGroup slideViewGroup = findViewById(R.id.scrollView);

        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(R.drawable.bg_red);

        slideViewGroup.addView(imageView);

        imageView = new ImageView(this);
        imageView.setBackgroundResource(R.drawable.bg_red);

        slideViewGroup.addView(imageView);
    }
}
