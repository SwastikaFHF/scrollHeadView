package com.aitangba.testproject.paging.view;

import com.aitangba.testproject.paging.PageBean;

/**
 * Created by fhf11991 on 2017/5/15.
 */

public class PagingHelper {

    private boolean isLoadingMore = false;  // 是否正在加载更多
    private boolean mHasMoreData = true;  //是否有更多数据，当没有更多数据时，不能进行自动加载更多

    private final PageBean mPageBean = new PageBean();

    boolean isPagingEnabled() {
        if (isLoadingMore) { // 避免多次滑动，重复触发加载更多的机制
            return false;
        }

        if (!mHasMoreData) {
            return false;
        }

        return isLoadingMore = true;
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
