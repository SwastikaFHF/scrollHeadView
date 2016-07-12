package com.aitangba.scrollheadview.slideback.slidinglayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.aitangba.scrollheadview.R;

/**
 * Created by nahuo16 on 2015/6/19.
 */
public class SlideBackActivity extends BaseSlideBackActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TouchLinearLayout layout = (TouchLinearLayout) getLayoutInflater().inflate(R.layout.activity_slideback , null);
        setSlideBack(layout);

        setContentView(layout);

    }


    public void more(View v){
        startActivity(new Intent(this , SlideBackActivity.class));
    }
}
