package com.aitangba.testproject.paging.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aitangba.testproject.paging.view.OnLoadMoreListener;

/**
 * Created by fhf11991 on 2017/5/11.
 */

public class EasyRecyclerView extends RecyclerView {

    private static final int TYPE_FOOTER_VIEW = 1002;//footer类型 Item
    private EasyAdapter mAdapter;
    private boolean hasTouchedScrollView = false; // 是否有触发滑动机制

    public EasyRecyclerView(Context context) {
        this(context, null);
    }

    public EasyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private OnLoadMoreListener mLoadMoreListener;
    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    public EasyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mOnBindFooterListener = new OnBindFooterListener() {
            @Override
            public ViewHolder onCreate(ViewGroup parent, int viewType) {
                TextView textView = new TextView(parent.getContext());
                textView.setText("测试数据");
                return new FooterViewHolder(textView);
            }

            @Override
            public void onBind(ViewHolder holder, int position) {

            }
        };

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    hasTouchedScrollView = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!hasTouchedScrollView) return;

                if(!canLoadMore()) {
                    return;
                }

                if(mAdapter != null && findLastVisibleItemPosition(getLayoutManager()) + 1 == mAdapter.getItemCount()) {
                    scrollLoadMore();
                }
            }
        });
    }

    private boolean isLoadingMore = false;// 是否正在加载更多
    private boolean canAutoLoadMore = true;//是否自动加载，当数据不满一屏幕会自动加载

    private boolean canLoadMore() {
        return mLoadMoreListener != null && !isLoadingMore && canAutoLoadMore;
    }

    @Override
    public void setLayoutManager(final LayoutManager layout) {
        super.setLayoutManager(layout);

        if (layout instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) layout);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if(mAdapter != null && mAdapter.isFooterView(position)) {
                        return gridManager.getSpanCount();
                    }
                    return 1;
                }
            });
        }
    }

    public void scrollLoadMore() {
        if (mLoadMoreListener != null) {
            isLoadingMore = true;
            mLoadMoreListener.onLoadMore(false);
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

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter == null) {
            return;
        }
        super.setAdapter(mAdapter = new EasyAdapter(adapter));
    }

    private class EasyAdapter extends RecyclerView.Adapter {

        private RecyclerView.Adapter mAdapter;

        public EasyAdapter(Adapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == TYPE_FOOTER_VIEW) {
                return mOnBindFooterListener.onCreate(parent, viewType);
            }
            return mAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if(holder instanceof FooterViewHolder) {
                mOnBindFooterListener.onBind(holder, position);
            }else {
                mAdapter.onBindViewHolder(holder, position);
            }
        }

        @Override
        public int getItemCount() {
            final int footerSize = mOnBindFooterListener == null ? 0 : 1;
            return mAdapter.getItemCount() + footerSize;
        }

        private boolean isFooterView(int position) {
            return position >= mAdapter.getItemCount() - 1;
        }

        @Override
        public int getItemViewType(int position) {
            if(position > mAdapter.getItemCount() - 1) {
                return TYPE_FOOTER_VIEW;
            }
            return mAdapter.getItemViewType(position);
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            mAdapter.registerAdapterDataObserver(observer);
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            mAdapter.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            mAdapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            mAdapter.onDetachedFromRecyclerView(recyclerView);
        }
    }

    private class FooterViewHolder extends ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    private OnBindFooterListener mOnBindFooterListener;

    public interface OnBindFooterListener {
        ViewHolder onCreate(ViewGroup parent, int viewType);
        void onBind(ViewHolder holder, int position);
    }
}
