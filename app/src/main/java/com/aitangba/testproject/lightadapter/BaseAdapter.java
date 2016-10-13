package com.aitangba.testproject.lightadapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
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
        int type = t.getType();
        mSparseArray.append(type, t);
        return type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mSparseArray.get(viewType).onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mItems.get(position).onBindViewHolder(holder);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    public interface Item<VH extends RecyclerView.ViewHolder> {

        int getType();

        int getItemViewId();

        VH onCreateViewHolder(ViewGroup viewGroup, int viewType);

        void onBindViewHolder(VH viewHolder);
    }

    public static class Items extends ArrayList<Item> {
    }
}