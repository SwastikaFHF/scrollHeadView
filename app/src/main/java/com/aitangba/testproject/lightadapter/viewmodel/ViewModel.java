package com.aitangba.testproject.lightadapter.viewmodel;

import android.support.v7.widget.RecyclerView;

/**
 * Created by fhf11991 on 2016/10/20.
 */

public interface ViewModel<VH extends RecyclerView.ViewHolder> {

    abstract int getViewType();

    abstract VH onCreateViewHolder();

    abstract void onBindViewHolder(VH viewHolder);
}
