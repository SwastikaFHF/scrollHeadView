package com.aitangba.testproject.view.calendar.common.listener;

import android.view.View;

import com.aitangba.testproject.view.calendar.common.CellAdapter;
import com.aitangba.testproject.view.calendar.common.CellBean;
import com.aitangba.testproject.view.calendar.common.MonthAdapter;

import java.util.Date;
import java.util.List;

/**
 * Created by fhf11991 on 2018/3/28.
 */

public class RangeChoiceListener implements CellAdapter.OnCellClickListener {

    private int mRangeSize;
    private MonthAdapter mMonthAdapter;

    public RangeChoiceListener(MonthAdapter monthAdapter, int rangeSize) {
        mMonthAdapter = monthAdapter;
        mRangeSize = rangeSize;
    }

    @Override
    public void onClick(CellAdapter cellAdapter, View cellView, CellBean cellBean) {
        List<CellBean> selectedCells = mMonthAdapter.getSelectedCell();
        int size = selectedCells.size();
        if (size == 0) {
            cellBean.isSelected = !cellBean.isSelected;
            cellAdapter.notifyDataSetChanged();
        } else if (size == 1) {
            CellBean selectedCell = selectedCells.get(0);
            Date selectedDate = selectedCell.date;
            Date date = cellBean.date;

            Date firstSelectedDate;
            Date lastSelectedDate;
            if (selectedDate.before(date)) {
                firstSelectedDate = selectedDate;
                lastSelectedDate = date;
            } else {
                firstSelectedDate = date;
                lastSelectedDate = selectedDate;
            }
            for (int i = 0, count = mMonthAdapter.getItemCount(); i < count; i++) {
                CellAdapter itemAdapter = mMonthAdapter.getItem(i);
                for (int j = 0, itemCount = itemAdapter.getCount(); j < itemCount; j++) {
                    CellBean tempCell = itemAdapter.getItem(j);
                    Date tempDate = tempCell.date;
                    if (!tempDate.before(firstSelectedDate) && !tempDate.after(lastSelectedDate)) {
                        tempCell.isSelected = true;
                    }
                }
            }
            mMonthAdapter.notifyDataSetChanged();
        } else {
            for (CellBean itemCell : selectedCells) {
                itemCell.isSelected = false;
            }
            cellBean.isSelected = true;
            mMonthAdapter.notifyDataSetChanged();
        }
    }
}
