package com.aitangba.testproject.slideback.slidinglayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.aitangba.testproject.R;


public class SlidingLayoutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_back);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext() , SlideBackActivity.class));
            }
        });
    }
}
