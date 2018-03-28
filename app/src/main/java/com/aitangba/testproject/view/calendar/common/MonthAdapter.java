package com.aitangba.testproject.view.calendar.common;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aitangba.testproject.R;
import com.aitangba.testproject.databinding.ItemCalendarCommonBinding;
import com.aitangba.testproject.view.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2018/3/27.
 */

public class MonthAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private List<CellAdapter> mList = new ArrayList();

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_calendar_common, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        ItemCalendarCommonBinding binding = holder.getBing();
        CellAdapter adapter = mList.get(position);
        binding.titleText.setText(adapter.getTitle());
        binding.monthView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setData(List<CellAdapter> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public CellAdapter getItem(int position) {
        return mList.get(position);
    }

    public final List<CellBean> getSelectedCell() {
        List<CellBean> list = new ArrayList<>();
        for (int i = 0, count = getItemCount(); i < count; i++) {
            CellAdapter itemAdapter = getItem(i);
            for (int j = 0, itemCount = itemAdapter.getCount(); j < itemCount; j++) {
                CellBean cellBean = itemAdapter.getItem(j);
                if(cellBean.isSelected) {
                    list.add(cellBean);
                }
            }
        }
        return list;
    }
}
