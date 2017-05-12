package com.aitangba.testproject.paging.effect;

import android.support.v4.widget.SwipeRefreshLayout;

import com.aitangba.testproject.paging.HttpTask;

/**
 * Created by fhf11991 on 2017/3/28.
 */

public class SwipeRefreshEffectImpl implements UIEffect{

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private SwipeRefreshEffectImpl(SwipeRefreshLayout swipeRefreshLayout) {
        mSwipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    public void onPreExecute(HttpTask httpTask) {

    }

    @Override
    public void onSuccess() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onError() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCancel() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public static SwipeRefreshEffectImpl build(SwipeRefreshLayout swipeRefreshLayout) {
        if(swipeRefreshLayout == null) {
            throw new RuntimeException("swipeRefreshLayout can not be null !!!!");
        }
        return new SwipeRefreshEffectImpl(swipeRefreshLayout);
    }
}
