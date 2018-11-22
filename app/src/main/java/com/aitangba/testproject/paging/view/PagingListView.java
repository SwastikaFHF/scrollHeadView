package com.aitangba.testproject.paging.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aitangba.testproject.R;
import com.aitangba.testproject.paging.PageBean;

import java.util.List;


/**
 * Created by fhf11991 on 2017/5/11.
 */

public class PagingListView extends ListView implements PagingManager {

    private OnScrollListener mOnScrollListener;

    private EasyAdapter mEasyAdapter;
    private View mEmptyView;

    private PagingHelper mPagingHelper = new PagingHelper();
    private OnLoadMoreListener mLoadMoreListener;

    public PagingListView(Context context) {
        this(context, null);
    }

    public PagingListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        super.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mOnScrollListener != null) {
                    mOnScrollListener.onScrollStateChanged(view, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 判断有没有滑动到最后一组数据
                if (mOnScrollListener != null && view.getLastVisiblePosition() + 1 != view.getCount()) {
                    mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }

                // 判断能不能加载更多
                if (mPagingHelper.isPagingEnabled() && mLoadMoreListener != null) {
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
        if (adapter == null) {
            return;
        }

        super.setAdapter(mEasyAdapter = new EasyAdapter(adapter));
    }

    @Override
    public void checkPaging(List array) {
        boolean hasMoreData = mPagingHelper.finishLoadMore(array.size());

        updateEmptyStatus();
        if (mEasyAdapter == null) {
            return;
        }

        mEasyAdapter.mFooterViewHelper.setMoreData(hasMoreData);
    }

    @Override
    public PageBean getPageBean() {
        return mPagingHelper.getPageBean();
    }

    @Override
    public void setEmptyView(View emptyView) {
        super.setEmptyView(emptyView);
        mEmptyView = emptyView;
    }

    private void updateEmptyStatus() {
        if (mEmptyView == null) {
            return;
        }

        if (mEasyAdapter == null) {
            return;
        }

        final boolean empty = (mEasyAdapter.mAdapter.isEmpty());

        if (empty) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    private static class EasyAdapter extends BaseAdapter {
        private final ListAdapter mAdapter;
        private final FooterViewHelper mFooterViewHelper = new FooterViewHelper();

        private EasyAdapter(ListAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public int getCount() {
            final int commonItemCount = mAdapter.getCount();
            if (commonItemCount == 0) {
                return 0;
            } else if (commonItemCount < 10) {
                return commonItemCount;
            } else {
                return commonItemCount + 1;
            }
        }

        @Override
        public Object getItem(int position) {
            final int commonItemCount = mAdapter.getCount();
            if (position < commonItemCount) {
                return mAdapter.getItem(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            final int commonItemCount = mAdapter.getCount();
            if (position < commonItemCount) {
                return mAdapter.getItemId(position);
            }
            return -1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int commonItemCount = mAdapter.getCount();
            if (position < commonItemCount) {
                return mAdapter.getView(position, convertView, parent);
            }
            mFooterViewHelper.onCreateView(parent);
            return mFooterViewHelper.itemView;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            mAdapter.registerDataSetObserver(observer);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            mAdapter.unregisterDataSetObserver(observer);
        }
    }

    private static class FooterViewHelper {

        public View itemView;
        private ProgressBar mProgressBar;
        private TextView mTextView;
        private boolean mHasMoreData;

        private void onCreateView(ViewGroup parent) {
            this.itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_footer_view, parent, false);
            mProgressBar = (ProgressBar) this.itemView.findViewById(R.id.footer_view_progressbar);
            mTextView = (TextView) this.itemView.findViewById(R.id.footer_view_tv);
        }

        private void setMoreData(boolean hasMoreData) {
            mHasMoreData = hasMoreData;

            refresh();
        }

        private void refresh() {
            if (itemView == null) {
                return;
            }
            if (mHasMoreData) {
                mProgressBar.setVisibility(View.VISIBLE);
                mTextView.setText("加载更多数据中");
            } else {
                mProgressBar.setVisibility(View.GONE);
                mTextView.setText("没有更多数据了");
            }
        }
    }
}
