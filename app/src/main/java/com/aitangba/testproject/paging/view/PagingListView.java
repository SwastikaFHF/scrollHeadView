package com.aitangba.testproject.paging.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.aitangba.testproject.R;
import com.aitangba.testproject.paging.PageBean;

import java.util.List;


/**
 * Created by fhf11991 on 2017/5/11.
 */

public class PagingListView extends ListView implements PagingManager {

    private OnScrollListener mOnScrollListener;
    private FooterViewHolder mFooterViewHolder;

    private PagingHelper mPagingHelper = new PagingHelper();
    private OnLoadMoreListener mLoadMoreListener;

    public PagingListView(Context context) {
        this(context, null);
    }

    public PagingListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View footerView = LayoutInflater.from(context).inflate(R.layout.layout_footer_view, null);
        setFooterViewHolder(new FooterViewHolder(footerView));

        super.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(mOnScrollListener != null) {
                    mOnScrollListener.onScrollStateChanged(view, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 判断有没有滑动到最后一组数据
                if(mOnScrollListener != null && view.getLastVisiblePosition() + 1 !=  view.getCount()) {
                    mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }

                // 判断能不能加载更多
                if(mPagingHelper.isPagingEnabled() && mLoadMoreListener != null) {
                    mLoadMoreListener.onLoadMore();
                }
            }
        });
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    @Override
    public void checkPaging(List array) {
        boolean hasMoreData = mPagingHelper.finishLoadMore(array.size());
        updateFooterStatus(hasMoreData);
    }

    @Override
    public void checkError(int errorType, boolean refresh) {

    }

    @Override
    public PageBean getPageBean() {
        return mPagingHelper.getPageBean();
    }

    public void setFooterViewHolder(FooterViewHolder footerViewHolder) {
        mFooterViewHolder = footerViewHolder;
        addFooterView(mFooterViewHolder.itemView);
    }

    private void updateFooterStatus(boolean hasMoreData) {
        if(mFooterViewHolder == null) {
            return;
        }
        mFooterViewHolder.bindView(hasMoreData);
    }
}
