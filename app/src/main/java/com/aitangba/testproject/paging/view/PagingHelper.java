package com.aitangba.testproject.paging.view;

import com.aitangba.testproject.paging.PageBean;

/**
 * Created by fhf11991 on 2017/5/15.
 */

public class PagingHelper {

    private boolean isLoadingMore = false;  // 是否正在加载更多
    private boolean mHasMoreData = true;  //是否有更多数据，当没有更多数据时，不能进行自动加载更多
    private boolean mIsAutoLoadEnabled = true;  //是否使用自动加载

    private final PageBean mPageBean = new PageBean();

    public void setAutoLoadEnabled(boolean enable) {
        mIsAutoLoadEnabled = enable;
    }

    public boolean isAutoLoadEnabled() {
        return mIsAutoLoadEnabled;
    }

    public void onScrolled(boolean isLastPosition) {
        if(!mIsAutoLoadEnabled) {
            return;
        }

        if(!isLastPosition) {
            return;
        }

        if(isLoadingMore) { //
            return;
        }

        if(!mHasMoreData) {
            return;
        }

        if (mLoadMoreListener != null) {
            isLoadingMore = true;
            mLoadMoreListener.onLoadMore(false);
        }
    }

    public void startLoad(boolean refresh) {
        if(refresh) {
            mPageBean.reset();
        } else {
            mPageBean.increase();
        }
    }

    public void finishLoadMore(boolean hasMoreData) {
        isLoadingMore = false;
        mHasMoreData = hasMoreData;
    }

    public PageBean getPageBean() {
        return mPageBean;
    }

    private int mDataSize;

    public boolean onChanged(int dataSize) {
        final int originSize = mDataSize;
        final int currentSize = dataSize;
        final int diffSize = currentSize - originSize;
        mDataSize = currentSize;

        if(isLoadingMore) { // 初步判断为加载更多数据的返回数据
            if(diffSize < 0) { // 对数据进行了删除操作,暂且认为还有更多数据，具体判断需要再次进行下拉刷新
                return true;
            } else if(diffSize < PageBean.PAGE_SIZE) { // 返回的数据不满足再次请求的条件
                return false;
            } else {
                return true;
            }
        } else {  // 对数据进行刷新
            if(mHasMoreData) { // 原来认为还有更多数据，再次刷新不影响之前的判断
                return true;
            } else {
                if(diffSize <= 0 ) { // 原来认为没有更多数据，再次刷新数据更少，任然认为没有更多数据
                    return false;
                } else if(diffSize < PageBean.PAGE_SIZE){ // 原来认为没有更多数据，再次刷新加载的新数据不满足分页条件
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    private OnLoadMoreListener mLoadMoreListener;

    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }
}
