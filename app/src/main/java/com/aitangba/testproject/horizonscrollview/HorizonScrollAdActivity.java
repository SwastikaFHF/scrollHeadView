package com.aitangba.testproject.horizonscrollview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
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

//        SlideViewGroup slideViewGroup = findViewById(R.id.scrollView);
//
//        ImageView imageView = new ImageView(this);
//        imageView.setBackgroundResource(R.drawable.bg_red);
//
//        slideViewGroup.addView(imageView);
//
//        imageView = new ImageView(this);
//        imageView.setBackgroundResource(R.drawable.bg_update_common);
//
//        slideViewGroup.addView(imageView);

        LoopViewPager loopViewPager = findViewById(R.id.loopViewPager);

        loopViewPager.setAdapter(new Adapter());
    }

    private class Adapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(container.getContext());
            if(position == 0) {
                imageView.setBackgroundResource(R.drawable.bg_red);
            } else {
                imageView.setBackgroundResource(R.drawable.bg_update_common);
            }
            container.addView(imageView);
            return imageView;
        }
    }
}
