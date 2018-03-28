package com.aitangba.testproject.view.calendar.common.listener;

import android.view.View;

import com.aitangba.testproject.view.calendar.common.CellAdapter;
import com.aitangba.testproject.view.calendar.common.CellBean;
import com.aitangba.testproject.view.calendar.common.MonthAdapter;

/**
 * Created by fhf11991 on 2018/3/27.
 */

public class SingleChoiceListener implements CellAdapter.OnCellClickListener {

    private MonthAdapter mMonthAdapter;

    public SingleChoiceListener(MonthAdapter monthAdapter) {
        mMonthAdapter = monthAdapter;
    }

    @Override
    public void onClick(CellAdapter cellAdapter, View cellView, CellBean cellBean) {
        for (int i = 0, count = mMonthAdapter.getItemCount(); i < count; i++) {
            CellAdapter itemAdapter = mMonthAdapter.getItem(i);
            for (int j = 0, itemCount = itemAdapter.getCount(); j < itemCount; j++) {
                itemAdapter.getItem(j).isSelected = false;
            }
        }
        cellBean.isSelected = !cellBean.isSelected;
        mMonthAdapter.notifyDataSetChanged();
    }
}
