package com.aitangba.testproject.slideback.slidingmenu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aitangba.testproject.R;
import com.aitangba.testproject.slideback.slidingmenu.lib.BaseSwipeBackActivity;

import java.util.Random;

public class SlidingMenuActivity extends BaseSwipeBackActivity {

    private int mCurrentIndex;

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
        containerRl.setBackgroundColor(Color.argb(255, red, green, blue));

        mCurrentIndex = getIntent().getIntExtra("index", 1);

        TextView textView = (TextView) findViewById(R.id.tv_content);
        textView.setText("这是第" + mCurrentIndex + "页");
    }

    public void nextPage(View v) {
        mCurrentIndex++;
        Intent intent = new Intent(this, SlidingMenuActivity.class);
        intent.putExtra("index", mCurrentIndex);
        startActivity(intent);
    }
}
