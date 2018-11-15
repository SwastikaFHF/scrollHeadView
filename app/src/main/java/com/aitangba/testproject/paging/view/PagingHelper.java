package com.aitangba.testproject.paging.view;

import com.aitangba.testproject.paging.PageBean;

/**
 * Created by fhf11991 on 2017/5/15.
 */

public class PagingHelper {

    private boolean isLoadingMore = false;  // 是否正在加载更多
    private boolean mHasMoreData = true;  //是否有更多数据，当没有更多数据时，不能进行自动加载更多
    private OnLoadMoreListener mLoadMoreListener;

    private final PageBean mPageBean = new PageBean();

    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    void onScrolled(boolean isLastPosition) {
        if (!isLastPosition) {
            return;
        }

        if (isLoadingMore) { //
            return;
        }

        if (!mHasMoreData) {
            return;
        }

        if (mLoadMoreListener != null) {
            isLoadingMore = true;
            mLoadMoreListener.onLoadMore();
        }
    }

    boolean finishLoadMore(int dataSize) {
        boolean hasMoreData = dataSize == PageBean.ORIGIN_PAGE_SIZE;

        //修正当前页
        if (dataSize == 0) {
            mPageBean.decline();
        }

        isLoadingMore = false;
        mHasMoreData = hasMoreData;

        return mHasMoreData;
    }

    PageBean getPageBean() {
        return mPageBean;
    }

}
