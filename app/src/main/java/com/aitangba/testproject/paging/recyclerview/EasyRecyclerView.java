package com.aitangba.testproject.paging.recyclerview;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.IntDef;
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
import com.aitangba.testproject.paging.view.OnLoadMoreListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by fhf11991 on 2017/5/11.
 */

public class EasyRecyclerView extends RecyclerView {

    private static final int TYPE_HEADER_VIEW = 1001;//header类型 Item
    private static final int TYPE_FOOTER_VIEW = 1002;//footer类型 Item

    private EasyAdapter mAdapter;
    private View mEmptyView;
    private View mHeaderView;
    private View mFooterView;

    private boolean hasTouchedScrollView = false; // 是否有触发滑动机制
    private boolean isLoadingMore = false;  // 是否正在加载更多
    private boolean mHasMoreData = true;  //是否有更多数据，当没有更多数据时，不能进行自动加载更多
    private boolean mEnableAutoLoadMore = true;  //是否使用自动加载

    private OnStateChangeListener mFooterStateChangeListener;
    private OnStateChangeListener mEmptyStateChangeListener;
    private OnLoadMoreListener mLoadMoreListener;

    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    public EasyRecyclerView(Context context) {
        this(context, null);
    }

    public EasyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        View footerView = LayoutInflater.from(context).inflate(R.layout.layout_footer_view, null);
        addFooterView(footerView, new OnStateChangeListener() {
            @Override
            public void onBind(View footerView, @State int state) {

            }
        });

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
                if(!hasTouchedScrollView) { //
                    return;
                }

                if(isLoadingMore) { //
                    return;
                }

                if(!mHasMoreData) {
                    return;
                }

                if(mAdapter != null && findLastVisibleItemPosition(getLayoutManager()) + 1 == mAdapter.getItemCount()) {
                    if (mLoadMoreListener != null) {
                        isLoadingMore = true;
                        mLoadMoreListener.onLoadMore(false);
                    }
                }
            }
        });
    }

    public void finishLoad(boolean hasMoreData) {
        isLoadingMore = false;
        mHasMoreData = hasMoreData;

        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setEmptyView(View emptyView, OnStateChangeListener onStateChangeListener) {
        mEmptyView = emptyView;
        mEmptyStateChangeListener = onStateChangeListener;

        updateEmptyStatus();
    }

    public void addHeaderView(View headerView) {
        mHeaderView = headerView;
        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void addFooterView(View footerView, OnStateChangeListener onStateChangeListener) {
        mFooterView = footerView;
        mFooterStateChangeListener = onStateChangeListener;

        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void updateEmptyStatus() {
        if(mEmptyView == null) {
            return;
        }

        final boolean empty = ((mAdapter == null) || mAdapter.isEmpty());
        final boolean hasNetwork = isNetworkConnected(getContext());

        int state;
        if(empty && !hasNetwork) {
            state = STATE_NO_NETWORK;
        } else if(empty && hasNetwork) {
            state = STATE_NO_DATA;
        } else {
            state = STATE_COMMON;
        }

        if(mEmptyStateChangeListener != null) {
            mEmptyStateChangeListener.onBind(mEmptyView, state);
        }
    }

    @Override
    public void setLayoutManager(final LayoutManager layout) {
        super.setLayoutManager(layout);

        if (layout instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) layout);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if(mAdapter != null && mAdapter.getItemViewType(position) == TYPE_FOOTER_VIEW) {
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

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter == null) {
            return;
        }
        super.setAdapter(mAdapter = new EasyAdapter(adapter));

        mAdapter.registerAdapterDataObserver(new AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updateEmptyStatus();
            }
        });
    }

    private class EasyAdapter extends RecyclerView.Adapter {

        private RecyclerView.Adapter mAdapter;

        public EasyAdapter(Adapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == TYPE_HEADER_VIEW) {
                return new ViewHolder(mHeaderView) {};
            } else if(viewType == TYPE_FOOTER_VIEW) {
                return new ViewHolder(mFooterView) {};
            }
            return mAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if(getItemViewType(position) == TYPE_HEADER_VIEW) {

            } else if(getItemViewType(position) == TYPE_FOOTER_VIEW) {
                if(mFooterStateChangeListener != null) {
                    int state;
                    if(mHasMoreData) {
                        state = STATE_COMMON;
                    } else {
                        state = STATE_NO_MORE_DATA;
                    }
                    mFooterStateChangeListener.onBind(mFooterView, state);
                }
            } else {
                mAdapter.onBindViewHolder(holder, position);
            }
        }

        @Override
        public int getItemCount() {
            final boolean hasHeader = mHeaderView != null;
            final boolean hasFooter = mEnableAutoLoadMore && mFooterView != null;

            final int commonItemCount = mAdapter.getItemCount();
            final int headerCount = hasHeader ? 1 : 0;
            final int footerCount;
            if(commonItemCount == 0) {
                footerCount = 0;
            } else {
                footerCount = hasFooter ? 1 : 0;
            }
            return commonItemCount + headerCount + footerCount;
        }

        @Override
        public int getItemViewType(int position) {
            final boolean hasHeader = mHeaderView != null;
            final int commonItemCount = mAdapter.getItemCount();

            if(hasHeader) {
                if(position == 0) {
                    return TYPE_HEADER_VIEW;
                } else if(position > 1 + (commonItemCount - 1)) {
                    return TYPE_FOOTER_VIEW;
                } else {
                    return mAdapter.getItemViewType(position);
                }
            } else {
                if(position > commonItemCount - 1) {
                    return TYPE_FOOTER_VIEW;
                } else {
                    return mAdapter.getItemViewType(position);
                }
            }
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

        protected boolean isEmpty() {
            return mAdapter.getItemCount() == 0;
        }

    }

    public interface OnStateChangeListener {
        void onBind(View view, @State int state);
    }


    @IntDef({STATE_COMMON, STATE_NO_NETWORK, STATE_NO_DATA, STATE_NO_MORE_DATA})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State{}

    public final static int STATE_COMMON = 1;
    public final static int STATE_NO_NETWORK = 2;
    public final static int STATE_NO_DATA = 3;
    public final static int STATE_NO_MORE_DATA = 4;

    /**
     * Check whether network is connected currently.
     *  please add a permission as: android.permission.ACCESS_NETWORK_STATE
     *
     * @param context application context
     * @return return true if network is connected, otherwise return false.
     */
    private final static boolean isNetworkConnected(Context context) {
        if (context == null) {
            throw new IllegalArgumentException();
        }

        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}
