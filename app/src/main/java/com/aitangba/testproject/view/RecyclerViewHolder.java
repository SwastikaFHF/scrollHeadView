package com.aitangba.testproject.view;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

/**
 * Created by fhf11991 on 2018/3/27.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private ViewDataBinding mBinding;

    public RecyclerViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        mBinding = binding;
    }

    public <T extends ViewDataBinding> T  getBing() {
        return (T)mBinding;
    }
}
