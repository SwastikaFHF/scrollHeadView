package com.aitangba.testproject.view.lightadapter.viewmodel;

import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2016/10/20.
 */

public class LightAdapter extends RecyclerView.Adapter<LightViewHolder> {

    private List<ViewModel> mList = new ArrayList<>();
    private SparseIntArray mSparseArray;

    public void setData(List<? extends ViewModel> items) {
        mList.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(mSparseArray == null) {
            mSparseArray = new SparseIntArray();
        }

        ViewModel t = mList.get(position);
        int type = t.getViewType();
        mSparseArray.append(type, position);
        return type;
    }

    @Override
    public LightViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final ViewModel item = mList.get(mSparseArray.get(viewType));
        return item.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(LightViewHolder holder, int position) {
        mList.get(position).onBindViewHolder(this, holder.mBinding);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
