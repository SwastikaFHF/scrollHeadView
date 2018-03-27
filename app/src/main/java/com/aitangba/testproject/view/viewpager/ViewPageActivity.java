package com.aitangba.testproject.view.viewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aitangba.testproject.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2016/7/1.
 */
public class ViewPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpage);

        List<GridViewGroup> gridViewGroupList = new ArrayList<>();
        GridViewGroup gridViewGroup = new GridViewGroup(this);
        gridViewGroup.setSizeLimit(3);
        GridViewGroup gridViewGroup2 = new GridViewGroup(this);
        gridViewGroup2.setSizeLimit(1);

        for (int i = 0; i < 16 ; i ++){
            TextView textView = new TextView(this);
            textView.setText("我是测试" + i);
            gridViewGroup.addView(textView);
        }
        for (int i = 0; i < 16 ; i ++){
            TextView textView = new TextView(this);
            textView.setText("我是测试" + i);
            gridViewGroup2.addView(textView);
        }


        gridViewGroupList.add(gridViewGroup);
        gridViewGroupList.add(gridViewGroup2);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_page);
        viewPager.setAdapter(new ViewAdapter(gridViewGroupList));
        viewPager.setCurrentItem(0);
    }

    static class ViewAdapter extends PagerAdapter {

        List<GridViewGroup> mViews;
        public ViewAdapter(List<GridViewGroup> views) {
            mViews = views;
        }

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViews.get(position));
            return mViews.get(position);
        }
    }
}
