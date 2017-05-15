package com.aitangba.testproject.paging.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aitangba.testproject.R;
import com.aitangba.testproject.paging.PageBean;


/**
 * Created by XBeats on 2017/3/26.
 */

public class PagingListView extends ListView implements PagingManager {

    private OnScrollListener mOnScrollListener;
    private View mFooterView;
    private FooterViewHolder holder;
    private PagingHelper mPagingHelper = new PagingHelper();

    public PagingListView(Context context) {
        this(context, null);
    }

    public PagingListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mFooterView = LayoutInflater.from(context).inflate(R.layout.layout_footer_view, null);
        addFooterView(mFooterView);

        super.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(mOnScrollListener != null) {
                    mOnScrollListener.onScrollStateChanged(view, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mOnScrollListener != null) {
                    mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }

                mPagingHelper.onScrolled(view.getLastVisiblePosition() + 1 ==  view.getCount());
            }
        });
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    @Override
    public void setAdapter(final ListAdapter adapter) {
        super.setAdapter(adapter);
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                finishLoadMore(mPagingHelper.onChanged(adapter.getCount()));
            }
        });
    }

    @Override
    public void setAutoLoadEnabled(boolean enable) {
        mPagingHelper.setAutoLoadEnabled(enable);
    }

    @Override
    public void startLoad(boolean refresh) {
        mPagingHelper.startLoad(refresh);
    }

    @Override
    public void finishLoadMore(boolean hasMoreData) {
        mPagingHelper.finishLoadMore(hasMoreData);

        updateFooterStatus(hasMoreData);
    }

    @Override
    public PageBean getPageBean() {
        return mPagingHelper.getPageBean();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mPagingHelper.setOnLoadMoreListener(onLoadMoreListener);
    }

    private void updateFooterStatus(boolean hasMoreData) {
        if(mFooterView == null) {
            return;
        }

        if(holder == null) {
            holder = new FooterViewHolder();
            holder.mProgressBar = (ProgressBar) mFooterView.findViewById(R.id.footer_view_progressbar);
            holder.mTextView = (TextView) mFooterView.findViewById(R.id.footer_view_tv);
        }

        if(hasMoreData) {
            holder.mProgressBar.setVisibility(View.VISIBLE);
            holder.mTextView.setText("加载更多数据中");
        } else {
            holder.mProgressBar.setVisibility(View.GONE);
            holder.mTextView.setText("没有更多数据了");
        }
    }
}
