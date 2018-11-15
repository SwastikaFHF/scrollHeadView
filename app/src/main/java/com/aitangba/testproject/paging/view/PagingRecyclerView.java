package com.aitangba.testproject.paging.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aitangba.testproject.R;
import com.aitangba.testproject.paging.PageBean;

import java.util.List;

/**
 * Created by fhf11991 on 2017/5/11.
 */

public class PagingRecyclerView extends RecyclerView implements PagingManager {

    private static final int TYPE_FOOTER_VIEW = 1002; //footer类型 Item

    private EasyAdapter mEasyAdapter;
    private View mEmptyView;
    private FooterViewHolder mFooterViewHolder;

    private PagingHelper mPagingHelper = new PagingHelper();
    private OnLoadMoreListener mLoadMoreListener;
    private AdapterDataObserver mEmptyAdapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            updateEmptyStatus();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            updateEmptyStatus();
        }
    };

    public PagingRecyclerView(Context context) {
        this(context, null);
    }

    public PagingRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagingRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFooterView(new FooterViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_footer_view, null)));

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // 判断有没有滑动到最后一组数据
                if(mEasyAdapter == null || findLastVisibleItemPosition(getLayoutManager()) + 1 != mEasyAdapter.getItemCount()) {
                    return;
                }

                // 判断能不能加载更多
                if(mPagingHelper.isPagingEnabled() && mLoadMoreListener != null) {
                    mLoadMoreListener.onLoadMore();
                }
            }
        });
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter == null) {
            return;
        }
        if(mEasyAdapter != null) {
            mEasyAdapter.unregisterAdapterDataObserver(mEmptyAdapterDataObserver);
        }
        super.setAdapter(mEasyAdapter = new EasyAdapter(adapter));
        mEasyAdapter.registerAdapterDataObserver(mEmptyAdapterDataObserver);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    @Override
    public void checkPaging(List array) {
        boolean hasMoreData = mPagingHelper.finishLoadMore(array.size());
        updateEmptyStatus();
        updateFooterStatus(hasMoreData);
    }

    @Override
    public PageBean getPageBean() {
        return mPagingHelper.getPageBean();
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;

        updateEmptyStatus();
    }

    private void updateEmptyStatus() {
        if(mEmptyView == null) {
            return;
        }

        if(mEasyAdapter == null) {
            return;
        }

        final boolean empty = (mEasyAdapter.mAdapter.getItemCount() == 0);

        if(empty) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    public void setFooterView(FooterViewHolder footerViewHolder) {
        mFooterViewHolder = footerViewHolder;
        if(mEasyAdapter != null) {
            mEasyAdapter.notifyDataSetChanged();
        }
    }

    private void updateFooterStatus(boolean hasMoreData) {
        if(mFooterViewHolder == null) {
            return;
        }
        mFooterViewHolder.bindView(hasMoreData);
    }

    @Override
    public void setLayoutManager(final LayoutManager layout) {
        super.setLayoutManager(layout);

        if (layout instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) layout);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if(mEasyAdapter != null && mEasyAdapter.getItemViewType(position) == TYPE_FOOTER_VIEW) {
                        return gridManager.getSpanCount();
                    }
                    return 1;
                }
            });
        }
    }

    private int findLastVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
            int max = lastVisibleItemPositions[0];
            for (int value : lastVisibleItemPositions) {
                if (value > max) {
                    max = value;
                }
            }
            return max;
        }
        return -1;
    }

    private class EasyAdapter extends RecyclerView.Adapter {

        private RecyclerView.Adapter mAdapter;

        private EasyAdapter(Adapter adapter) {
            mAdapter = adapter;
        }

        @Override
        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType == TYPE_FOOTER_VIEW) {
                return new ViewHolder(mFooterViewHolder.itemView) {};
            }
            return mAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if(getItemViewType(position) == TYPE_FOOTER_VIEW) {

            } else {
                mAdapter.onBindViewHolder(holder, position);
            }
        }

        @Override
        public int getItemCount() {
            final boolean hasFooter = mFooterViewHolder.itemView != null;
            final int commonItemCount = mAdapter.getItemCount();
            final int footerCount;
            if(commonItemCount == 0) {
                footerCount = 0;
            } else {
                footerCount = hasFooter ? 1 : 0;
            }
            return commonItemCount + footerCount;
        }

        @Override
        public int getItemViewType(int position) {
            final int commonItemCount = mAdapter.getItemCount();

            if(position > commonItemCount - 1) {
                return TYPE_FOOTER_VIEW;
            } else {
                return mAdapter.getItemViewType(position);
            }
        }

        @Override
        public void registerAdapterDataObserver(@NonNull AdapterDataObserver observer) {
            mAdapter.registerAdapterDataObserver(observer);
        }

        @Override
        public void unregisterAdapterDataObserver(@NonNull AdapterDataObserver observer) {
            mAdapter.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            mAdapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
            mAdapter.onDetachedFromRecyclerView(recyclerView);
        }
    }
}
