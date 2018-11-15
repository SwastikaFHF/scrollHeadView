package com.aitangba.testproject.view.pagingadapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.aitangba.testproject.R;
import com.aitangba.testproject.databinding.LayoutFooterViewBinding;
import com.aitangba.testproject.paging.PageBean;
import com.aitangba.testproject.paging.view.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XBeats on 2017/3/26.
 */

public abstract class BasePagingAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> implements PagingManager {

    public static final int TYPE_COMMON_VIEW = 1001;//普通类型 Item
    public static final int TYPE_FOOTER_VIEW = 1002;//footer类型 Item

    private OnLoadMoreListener mLoadMoreListener;

    protected List<T> mList = new ArrayList<>();

    private boolean canAutoLoadMore = true;//是否自动加载，当数据不满一屏幕会自动加载

    private volatile int originSize = 0;

    @Override
    public void setNeverLoadMore(boolean neverLoadMore) {
        canAutoLoadMore = !neverLoadMore;
    }

    private boolean isLoadingMore = false;// 是否正在加载更多

    @Override
    public void finishLoadMore() {
        isLoadingMore = false;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_FOOTER_VIEW:
                ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_footer_view, parent, false);
                viewHolder = new RecyclerViewHolder(binding);
                break;
            case TYPE_COMMON_VIEW:
                viewHolder = onCreateCommonViewHolder(parent, viewType);
                break;
        }
        return viewHolder;
    }

    public abstract RecyclerViewHolder onCreateCommonViewHolder(ViewGroup parent, int viewType);

    public void onBindFooterViewHolder(RecyclerViewHolder holder, int position) {
        if(holder.mBinding instanceof LayoutFooterViewBinding) {
            LayoutFooterViewBinding binding = (LayoutFooterViewBinding) holder.mBinding;
            if(canAutoLoadMore) {
                binding.footerViewProgressbar.setVisibility(View.VISIBLE);
                binding.footerViewTv.setText("加载更多数据中");
            } else {
                binding.footerViewProgressbar.setVisibility(View.GONE);
                binding.footerViewTv.setText("暂无更多数据");
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooterView(position)) {
            return TYPE_FOOTER_VIEW;
        } else {
            return TYPE_COMMON_VIEW;
        }
    }

    /**
     * 是否是FooterView
     *
     * @param position
     * @return
     */
    private boolean isFooterView(int position) {
        return position >= getItemCount() - 1;
    }

    /**
     * StaggeredGridLayoutManager模式时，FooterView可占据一行
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (isFooterView(holder.getLayoutPosition())) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();

            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }

    private boolean hasTouchedScrollView = false; // 是否有触发滑动机制

    /**
     * GridLayoutManager模式时， FooterView可占据一行，判断RecyclerView是否到达底部
     *
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) layoutManager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isFooterView(position)) {
                        return gridManager.getSpanCount();
                    }
                    return 1;
                }
            });
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    hasTouchedScrollView = true;
                }

                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    return;
                }

                if(!canLoadMore()) {
                    return;
                }

                if (findLastVisibleItemPosition(layoutManager) + 1 == getItemCount()) {
                    scrollLoadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!hasTouchedScrollView) return;

                if(!canLoadMore()) {
                    return;
                }

                if (findLastVisibleItemPosition(layoutManager) + 1 == getItemCount()) {
                    scrollLoadMore();
                }
            }
        });
    }

    private boolean canLoadMore() {
        return mLoadMoreListener != null && !isLoadingMore && canAutoLoadMore;
    }

    /**
     * 到达底部开始刷新
     */
    @Override
    public void scrollLoadMore() {
        if (mLoadMoreListener != null) {
            isLoadingMore = true;
            mLoadMoreListener.onLoadMore();
        }
    }

    private int findLastVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
            return findMax(lastVisibleItemPositions);
        }
        return -1;
    }

    /**
     * 刷新加载更多的数据
     * @param list
     * @param refresh
     */
    public void addData(List<T> list, boolean refresh) {
        if(refresh) {
            mList.clear();
            originSize = 0;
            mList.addAll(list);
            notifyDataSetChanged();
        } else {
            final int size = mList.size();
            originSize = size;
            mList.addAll(list);
            notifyItemInserted(size);
        }

        final int currentSize = mList.size();
        if(currentSize - originSize < PageBean.ORIGIN_PAGE_SIZE) {
            setNeverLoadMore(true);
        } else {
            setNeverLoadMore(false);
        }
        if(mOnDataChangeListener != null) {
            mOnDataChangeListener.onChanged(currentSize, originSize);
        }
        originSize = currentSize;
    }

    private OnDataChangeListener mOnDataChangeListener;

    @Override
    public void setOnDataChangedListener(OnDataChangeListener onDataChangeListener) {
        mOnDataChangeListener = onDataChangeListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    private static int findMax(int[] lastVisiblePositions) {
        int max = lastVisiblePositions[0];
        for (int value : lastVisiblePositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
