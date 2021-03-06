package com.aitangba.testproject.view.lightadapter.viewmodel;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by fhf11991 on 2016/10/21.
 */

public class LightViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

    public T mBinding;

    public LightViewHolder(View itemView) {
        super(itemView);
        mBinding = DataBindingUtil.bind(itemView);
    }
}