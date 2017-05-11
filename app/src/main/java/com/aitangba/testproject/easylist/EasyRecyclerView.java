package com.aitangba.testproject.easylist;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by fhf11991 on 2017/5/11.
 */

public class EasyRecyclerView extends RecyclerView {

    private Adapter mAdapter;
    private View mHeaderView;
    private View mFooterView;
    private View mEmptyView;

    public EasyRecyclerView(Context context) {
        super(context);
    }

    public EasyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EasyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addHeaderView(View headerView) {
        mHeaderView = headerView;
        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void addFooterView(View footerView) {
        mFooterView = footerView;
        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter == null) {
            return;
        }

        super.setAdapter(mAdapter = new EasyAdapter(adapter));
    }

    private final static int TYPE_HEADER = -101;
    private final static int TYPE_FOOTER = -102;
    private final static int TYPE_EMPTY = -103;
    private class EasyAdapter extends RecyclerView.Adapter {

        private RecyclerView.Adapter mExtraAdapter;

        public EasyAdapter(Adapter extraAdapter) {
            mExtraAdapter = extraAdapter;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            final int headerSize = mHeaderView == null ? 0 : 1;
            final int footerSize = mFooterView == null ? 0 : 1;
            final int emptySize = mEmptyView == null ? 0 : 1;

            int count = mExtraAdapter.getItemCount();
            if(count == 0) {
                return count + headerSize + emptySize;
            }
            return count + headerSize + footerSize;
        }

        @Override
        public int getItemViewType(int position) {
            final int headerSize = mHeaderView == null ? 0 : 1;
            int count = mExtraAdapter.getItemCount();
            if(count == 0) {
                if(position == 0) {

                }
            }
            if(position == 0 && mHeaderView != null) {

            }

            return super.getItemViewType(position);
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            mExtraAdapter.registerAdapterDataObserver(observer);
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            mExtraAdapter.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            mExtraAdapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            mExtraAdapter.onDetachedFromRecyclerView(recyclerView);
        }
    }

    private class RecyclerViewDataObserver extends AdapterDataObserver {

    }
}
