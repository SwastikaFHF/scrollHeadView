package com.aitangba.testproject.slideback.slidingpanelayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.aitangba.testproject.R;

import java.util.Random;

public class SlidingPaneActivity extends BaseSlideBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        LinearLayout containerRl = (LinearLayout) findViewById(R.id.container);

        //随机色

        Random random = new Random();
        int red = random.nextInt(255);
        int green = random.nextInt(255);
        int blue = random.nextInt(255);

        containerRl.setBackgroundColor(Color.argb(255,red,green,blue));

    }

    public void nextPage(View v) {
        startActivity(new Intent(this, SlidingPaneActivity.class));
    }

}
