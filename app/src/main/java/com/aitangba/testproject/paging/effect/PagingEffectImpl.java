package com.aitangba.testproject.paging.effect;

import com.aitangba.testproject.paging.OnDataChangeListener;
import com.aitangba.testproject.paging.PageBean;
import com.aitangba.testproject.paging.view.PagingManager;
import com.aitangba.testproject.paging.helper.StatefulViewHelper;

/**
 * Created by fhf11991 on 2017/3/28.
 */

public class PagingEffectImpl implements UIEffect {

    private PagingManager mPagingManager;

    private PagingEffectImpl(PagingManager pagingManager) {
        mPagingManager = pagingManager;
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onSuccess() {
        mPagingManager.finishLoadMore();
    }

    @Override
    public void onError() {
        mPagingManager.finishLoadMore();
    }

    @Override
    public void onCancel() {
        mPagingManager.finishLoadMore();
    }

    public void bindStatefulUI(final StatefulViewHelper statefulViewHelper) {
        mPagingManager.setOnDataChangedListener(new OnDataChangeListener() {
            @Override
            public void onChanged(int currentSize, int oldSize) {
                statefulViewHelper.setCurrentSize(currentSize);
            }
        });
    }

    public static PagingEffectImpl build(PagingManager pagingManager) {
        if(pagingManager == null) {
            throw new RuntimeException("pagingManager can be null !!!!");
        }
        return new PagingEffectImpl(pagingManager);
    }
}
