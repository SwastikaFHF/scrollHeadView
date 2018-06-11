package com.aitangba.testproject.view.calendar.common.manager;

import android.view.View;

import com.aitangba.testproject.view.calendar.common.pojo.CellBean;

import java.util.Date;
import java.util.List;

/**
 * Created by fhf11991 on 2018/6/5
 */
public class MultipleChoiceManager extends BaseHolidayManager {


    private int mMultipleSize;

    public MultipleChoiceManager(int multipleSize) {
        mMultipleSize = multipleSize;
    }

    @Override
    public void onClick(View cellView, CellBean cellBean) {
        if(cellBean.isSelected) {
            cellBean.isSelected = false;
            monthAdapter.notifyDataSetChanged();
            return;
        }

        if(mMultipleSize == -1) {
            cellBean.isSelected = true;
            monthAdapter.notifyDataSetChanged();
            return;
        }

        // 清理超出范围的日期
        List<CellBean> selectedCells = monthAdapter.getSelectedCell();
        int size = selectedCells.size();
        Date date = cellBean.date;
        if(size == 0) {
            cellBean.isSelected = true;
            monthAdapter.notifyDataSetChanged();
        } else if(size == 1) {
            CellBean firstSelectedCell = selectedCells.get(0);
            Date firstSelectedDate = firstSelectedCell.date;
            if(firstSelectedDate.before(date) && !firstSelectedDate.after(getDateLater(date, -1 * mMultipleSize))) {
                firstSelectedCell.isSelected = false;
            } else if(firstSelectedDate.after(date) && !firstSelectedDate.before(getDateLater(date, mMultipleSize))) {
                firstSelectedCell.isSelected = false;
            }
            cellBean.isSelected = true;
            monthAdapter.notifyDataSetChanged();
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
            monthAdapter.notifyDataSetChanged();
        }
    }
}
