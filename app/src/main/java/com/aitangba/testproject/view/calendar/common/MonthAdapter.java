package com.aitangba.testproject.view.calendar.common;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aitangba.testproject.R;
import com.aitangba.testproject.view.calendar.common.manager.BaseCellManager;
import com.aitangba.testproject.view.calendar.common.pojo.CellBean;
import com.aitangba.testproject.view.calendar.common.pojo.WeekBean;
import com.aitangba.testproject.view.calendar.common.view.CalendarCellView;
import com.aitangba.testproject.view.calendar.common.view.WeekView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2018/3/27.
 */

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.WeekViewHolder> {

    private List<WeekBean> mList = new ArrayList();

    private LayoutInflater mLayoutInflater;

    private LayoutInflater getLayoutInflater(Context context) {
        if(mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(context);
        }
        return mLayoutInflater;
    }

    private BaseCellManager mBaseCellManager;

    public void setBaseCellManager(BaseCellManager baseCellManager) {
        mBaseCellManager = baseCellManager;
    }

    @Override
    public WeekViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WeekViewHolder(getLayoutInflater(parent.getContext()).inflate(R.layout.calendar_week_item, parent, false));
    }

    @Override
    public void onBindViewHolder(WeekViewHolder holder, int position) {
        WeekBean item = mList.get(position);

        if(item.title != null) {
            holder.titleText.setVisibility(View.VISIBLE);
            holder.titleText.setText(item.title);
        } else {
            holder.titleText.setVisibility(View.GONE);
        }

        int cellDateCount = item.cellBeans.size();
        for(int i = 0, count = holder.mWeekViews.size(); i < count; i++) {
            CellViewHolder cellViewHolder = holder.mWeekViews.get(i);
            if((item.spacingColumn != 0 && i < item.spacingColumn) || (item.spacingColumn == 0 && i >= cellDateCount)) {
                cellViewHolder.cellView.setChecked(false);
                cellViewHolder.cellView.setEnabled(false);
                cellViewHolder.cellView.setOption(CellBean.OPTION_NONE);

                cellViewHolder.title.setText("");
                cellViewHolder.flag.setText("");
                cellViewHolder.gregorianDayText.setText("");

                cellViewHolder.cellView.setOnClickListener(null);
            } else {
                CellBean cellBean = item.cellBeans.get(i - item.spacingColumn);
                cellViewHolder.cellView.setChecked(cellBean.isSelected);
                cellViewHolder.cellView.setWeekend(cellBean.isWeekend);
                cellViewHolder.cellView.setEnabled(cellBean.enable);
                cellViewHolder.cellView.setOption(cellBean.option);

                cellViewHolder.title.setText(String.valueOf(cellBean.index));
                cellViewHolder.flag.setText(TextUtils.isEmpty(cellBean.flag) ? "" : cellBean.flag);
                cellViewHolder.gregorianDayText.setText(TextUtils.isEmpty(cellBean.holiday) ? "" : cellBean.holiday);

                cellViewHolder.cellView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mBaseCellManager != null) {
                            mBaseCellManager.onClick(v, cellBean);
                        }
                    }
                });
            }

        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setData(List<WeekBean> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public WeekBean getItem(int position) {
        return mList.get(position);
    }

    public final List<CellBean> getSelectedCell() {
        List<CellBean> list = new ArrayList<>();
        for (int i = 0, count = getItemCount(); i < count; i++) {
            WeekBean itemAdapter = getItem(i);
            for (int j = 0, itemCount = itemAdapter.cellBeans.size(); j < itemCount; j++) {
                CellBean cellBean = itemAdapter.cellBeans.get(j);
                if(cellBean.isSelected) {
                    list.add(cellBean);
                }
            }
        }
        return list;
    }

    public static class WeekViewHolder extends RecyclerView.ViewHolder {

        private List<CellViewHolder> mWeekViews = new ArrayList<>();
        private WeekView weekView;
        private TextView titleText;
        public WeekViewHolder(View itemView) {
            super(itemView);
            weekView = itemView.findViewById(R.id.weekView);
            titleText = itemView.findViewById(R.id.titleText);
            CellViewHolder cellViewHolder;
            for(int i = 0, count = weekView.getChildCount(); i < count; i++) {
                View child = weekView.getChildAt(i);
                cellViewHolder = new CellViewHolder();

                cellViewHolder.cellView = (CalendarCellView) child;
                cellViewHolder.title = child.findViewById(R.id.dayText);
                cellViewHolder.flag = child.findViewById(R.id.flagText);
                cellViewHolder.gregorianDayText = child.findViewById(R.id.gregorianDayText);
                mWeekViews.add(cellViewHolder);
            }
        }
    }

    public static class CellViewHolder {
        private CalendarCellView cellView;
        private TextView title;
        private TextView flag;
        private TextView gregorianDayText;
    }
}
