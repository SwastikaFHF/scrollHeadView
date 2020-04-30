package com.aitangba.testproject.view.recyclerroll;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.aitangba.testproject.R;

/**
 * Created by XBeats on 2020/4/29
 * https://github.com/Marksss/InfiniteBanner
 */
public class RecyclableViewTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclable_view);

        RecyclableViewGroup recyclableViewGroup = findViewById(R.id.recyclableViewGroup);

        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.bg_red);
        recyclableViewGroup.addView(imageView);

        imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.bg_update_common);
        recyclableViewGroup.addView(imageView);
        recyclableViewGroup.start();

        CustomViewGroup customViewGroup = findViewById(R.id.customView);
        imageView = new ImageView(this);
        imageView.setBackgroundColor(Color.RED);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.bg_red);
        customViewGroup.addView(imageView);

        imageView = new ImageView(this);
        imageView.setBackgroundColor(Color.BLUE);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.bg_update_common);
        customViewGroup.addView(imageView);

    }
}
