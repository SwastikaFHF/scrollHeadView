package com.aitangba.testproject.view.calendar.common.listener;

import android.view.View;

import com.aitangba.testproject.view.calendar.common.CellAdapter;
import com.aitangba.testproject.view.calendar.common.CellBean;
import com.aitangba.testproject.view.calendar.common.MonthAdapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by fhf11991 on 2018/3/28.
 */

public class MultipleChoiceListener implements CellAdapter.OnCellClickListener {

    private MonthAdapter mMonthAdapter;
    private int mMultipleSize;

    public MultipleChoiceListener(MonthAdapter monthAdapter, int multipleSize) {
        mMonthAdapter = monthAdapter;
        mMultipleSize = multipleSize;
    }

    @Override
    public void onClick(CellAdapter cellAdapter, View cellView, CellBean cellBean) {
        if(cellBean.isSelected) {
            cellBean.isSelected = false;
            cellAdapter.notifyDataSetChanged();
            return;
        }

        if(mMultipleSize == -1) {
            cellBean.isSelected = true;
            cellAdapter.notifyDataSetChanged();
            return;
        }

        // 清理超出范围的日期
        List<CellBean> selectedCells = mMonthAdapter.getSelectedCell();
        int size = selectedCells.size();
        Date date = cellBean.date;
        if(size == 0) {
            cellBean.isSelected = true;
            cellAdapter.notifyDataSetChanged();
        } else if(size == 1) {
            CellBean firstSelectedCell = selectedCells.get(0);
            Date firstSelectedDate = firstSelectedCell.date;
            if(firstSelectedDate.before(date) && !firstSelectedDate.after(getDateLater(date, -1 * mMultipleSize))) {
                firstSelectedCell.isSelected = false;
            } else if(firstSelectedDate.after(date) && !firstSelectedDate.before(getDateLater(date, mMultipleSize))) {
                firstSelectedCell.isSelected = false;
            }
            cellBean.isSelected = true;
            mMonthAdapter.notifyDataSetChanged();
        } else {
            Date firstSelectedDate = selectedCells.get(0).date;
            Date lastSelectedDate = selectedCells.get(size - 1).date;
            if(date.before(firstSelectedDate)) {
                Date limitDate = getDateLater(date, mMultipleSize);
                for(CellBean itemCell : selectedCells) {
                    if (!itemCell.date.before(limitDate)) {
                        itemCell.isSelected = false;
                    }
                }
            } else if(date.after(lastSelectedDate)) {
                Date limitDate = getDateLater(date, -1 * mMultipleSize);
                for(CellBean itemCell : selectedCells) {
                    if (!itemCell.date.after(limitDate)) {
                        itemCell.isSelected = false;
                    }
                }
            }
            cellBean.isSelected = true;
            mMonthAdapter.notifyDataSetChanged();
        }
    }

    private Date getDateLater(Date date, int dateNum) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, dateNum);
        return cal.getTime();
    }
}
