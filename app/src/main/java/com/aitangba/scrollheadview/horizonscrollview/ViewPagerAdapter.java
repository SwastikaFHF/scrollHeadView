package com.aitangba.scrollheadview.horizonscrollview;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2016/9/13.
 */
public class ViewPagerAdapter extends PagerAdapter {

    private List<View> mViews = new ArrayList<>();

    public void setData(List<View> views) {
        mViews.clear();
        if(mViews != null) {
            mViews.addAll(views);
        }

        notifyDataSetChanged();
    }

    public void addData(List<View> views) {
        if(mViews != null) {
            mViews.addAll(views);
        }

        notifyDataSetChanged();
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
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViews.get(position % mViews.size()), 0);
        return mViews.get(position % mViews.size());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position % mViews.size()));
    }
}
