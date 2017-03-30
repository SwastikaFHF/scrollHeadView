package com.aitangba.testproject.paging.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aitangba.testproject.R;
import com.aitangba.testproject.paging.OnDataChangeListener;
import com.aitangba.testproject.paging.PageBean;


/**
 * Created by XBeats on 2017/3/26.
 */

public class PagingListView extends ListView implements PagingManager {

    private OnLoadMoreListener mLoadMoreListener;
    private boolean isLoading = false;// 是否正在加载
    private boolean canAutoLoadMore = true;
    private String noDataTips;
    private View progressBar;
    private TextView loadTisText;
    private OnScrollListener mOnScrollListener;
    private volatile int originSize = 0;
    private boolean hasTouchedScrollView = false; // 是否有触发滑动机制

    public PagingListView(Context context) {
        this(context, null);
    }

    public PagingListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View footerView = LayoutInflater.from(context).inflate(R.layout.layout_footer_view, null);
        progressBar = footerView.findViewById(R.id.footer_view_progressbar);
        loadTisText = (TextView) footerView.findViewById(R.id.footer_view_tv);
        addFooterView(footerView);
        super.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(mOnScrollListener != null) {
                    mOnScrollListener.onScrollStateChanged(view, scrollState);
                }

                if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    hasTouchedScrollView = true;
                }

                if (scrollState != OnScrollListener.SCROLL_STATE_IDLE) {
                    return;
                }

                if(!canLoadMore()) return;

                if (view.getLastVisiblePosition() + 1 ==  view.getCount()) {
                    scrollLoadMore();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mOnScrollListener != null) {
                    mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }

                if(!hasTouchedScrollView) return;

                if(!canLoadMore()) return;

                if (view.getLastVisiblePosition() + 1 ==  view.getCount()) {
                    scrollLoadMore();
                }
            }
        });
    }

    public void setNoDataTips(String noDataTips) {
        this.noDataTips = noDataTips;
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    private void notifyFooterView() {
        if(canAutoLoadMore) {
            loadTisText.setText("加载更多数据中");
            progressBar.setVisibility(VISIBLE);
        } else {
            if(!TextUtils.isEmpty(noDataTips)) {
                loadTisText.setText(noDataTips);
            } else {
                loadTisText.setText("暂无更多信息");
            }
            progressBar.setVisibility(GONE);
        }
    }

    public boolean isLoading() {
        return isLoading;
    }

    private boolean canLoadMore() {
        return mLoadMoreListener != null && !isLoading && canAutoLoadMore;
    }

    @Override
    public void setNeverLoadMore(boolean neverLoadMore) {
        canAutoLoadMore = !neverLoadMore;
        notifyFooterView();
    }

    @Override
    public void scrollLoadMore() {
        isLoading = true;
        if (mLoadMoreListener != null) {
            mLoadMoreListener.onLoadMore(false);
        }
    }

    @Override
    public void finishLoadMore() {
        isLoading = false;
        notifyFooterView();
    }

    @Override
    public void setAdapter(final ListAdapter adapter) {
        super.setAdapter(adapter);
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(adapter.getCount() - originSize < PageBean.PAGE_SIZE) {
                    setNeverLoadMore(true);
                } else {
                    setNeverLoadMore(false);
                }

                if(mOnDataChangeListener != null) {
                    mOnDataChangeListener.onChanged(adapter.getCount(), originSize);
                }
                originSize = adapter.getCount();
            }
        });
    }

    private OnDataChangeListener mOnDataChangeListener;

    @Override
    public void setOnDataChangedListener(OnDataChangeListener onDataChangeListener) {
        mOnDataChangeListener = onDataChangeListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mLoadMoreListener = mOnLoadMoreListener;
    }
}
