package com.aitangba.testproject.paging.effect;


import com.aitangba.testproject.paging.PageBean;
import com.aitangba.testproject.paging.Request;
import com.aitangba.testproject.paging.Response;
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
    public void onPreExecute(Request request) {
        mPagingManager.startLoad(mRefresh);
        PageBean pageBean = mPagingManager.getPageBean();
        if(mRefresh) {
            pageBean.reset();
        } else {
            pageBean.increase();
        }
        request.pageIndx = pageBean.pageIndex;
        request.pageSize = pageBean.pageSize;
    }

    @Override
    public void onSuccess(Response response) {
    }

    @Override
    public void onError(Response response) {
        mPagingManager.checkPaging(null);
    }

    @Override
    public void onCancel() {
        mPagingManager.checkPaging(null);
    }

    public static PagingEffectImpl build(PagingManager pagingManager, boolean refresh) {
        if(pagingManager == null) {
            throw new RuntimeException("pagingManager can be null !!!!");
        }
        return new PagingEffectImpl(pagingManager, refresh);
    }
}
