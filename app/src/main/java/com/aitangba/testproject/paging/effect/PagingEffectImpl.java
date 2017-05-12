package com.aitangba.testproject.paging.effect;


import com.aitangba.testproject.paging.HttpTask;
import com.aitangba.testproject.paging.view.PagingManager;

/**
 * Created by fhf11991 on 2017/3/28.
 */

public class PagingEffectImpl implements UIEffect {

    private boolean mRefresh;
    private PagingManager mPagingManager;

    private PagingEffectImpl(PagingManager pagingManager, boolean refresh) {
        mPagingManager = pagingManager;
        mRefresh = refresh;
    }

    @Override
    public void onPreExecute(HttpTask httpTask) {
        mPagingManager.startLoad(mRefresh);
        httpTask.setPageIndex(mPagingManager.getPageIndex());
    }

    @Override
    public void onSuccess() {
        mPagingManager.checkPaging(0);
    }

    @Override
    public void onError() {
        mPagingManager.finishLoadMore(true);
    }

    @Override
    public void onCancel() {
        mPagingManager.finishLoadMore(true);
    }

    public static PagingEffectImpl build(PagingManager pagingManager, boolean refresh) {
        if(pagingManager == null) {
            throw new RuntimeException("pagingManager can be null !!!!");
        }
        return new PagingEffectImpl(pagingManager, refresh);
    }
}
