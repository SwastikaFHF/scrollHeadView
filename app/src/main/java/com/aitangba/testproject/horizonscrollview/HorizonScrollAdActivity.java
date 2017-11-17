package com.aitangba.testproject.horizonscrollview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aitangba.testproject.R;
import com.aitangba.testproject.slideback.slidinglayout.BaseActivity;

import java.util.ArrayList;
import java.util.List;

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

        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.bg_red);
        list.add(R.drawable.bg_red);
        final Adapter adapter = new Adapter(list);
        final LoopViewPager loopViewPager = findViewById(R.id.loopViewPager);
        loopViewPager.setAdapter(adapter);


        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> list = new ArrayList<>();
                list.add(R.drawable.bg_update_common);
                list.add(R.drawable.bg_update_common);
                final Adapter adapter = new Adapter(list);
                loopViewPager.setAdapter(adapter);
            }
        });
    }

    private class Adapter extends PagerAdapter {

        private List<Integer> mList;


        public Adapter(List<Integer> list) {
            mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
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
            imageView.setBackgroundResource(mList.get(position));
            container.addView(imageView);
            return imageView;
        }
    }
}
