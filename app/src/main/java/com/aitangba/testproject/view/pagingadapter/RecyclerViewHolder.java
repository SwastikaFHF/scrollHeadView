package com.aitangba.testproject.view.pagingadapter;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by XBeats on 2017/3/26.
 */

public final class RecyclerViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {
    public ViewDataBinding mBinding;

    public RecyclerViewHolder(T t) {
        super(t.getRoot());
        mBinding = t;
    }
}
