package com.aitangba.testproject.view.lightadapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2016/10/13.
 */

public class BaseAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Items mItems = new Items();
    private SparseArray<Item> mSparseArray;

    public void setData(List<? extends Item> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(mSparseArray == null) {
            mSparseArray = new SparseArray<>();
        }
        Item t = mItems.get(position);
        int type = t.getItemViewId();
        mSparseArray.append(type, t);
        return type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Item item = mSparseArray.get(viewType);
        return item.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mItems.get(position).onBindViewHolder(this, holder);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    public interface Item<VH extends RecyclerView.ViewHolder> {

        int getItemViewId();

        VH onCreateViewHolder(ViewGroup parent, int viewType);

        void onBindViewHolder(BaseAdapter baseAdapter, VH viewHolder);

    }

    public static class Items extends ArrayList<Item> {
    }


    private static class LightViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

        public T mBinding;

        public LightViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }
    }
}