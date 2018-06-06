package com.aitangba.testproject.view.calendar.common.manager;

import android.view.View;

import com.aitangba.testproject.view.calendar.common.BaseCellAdapter;
import com.aitangba.testproject.view.calendar.common.CellBean;

import java.util.Date;
import java.util.List;

/**
 * Created by fhf11991 on 2018/6/5
 */
public class RangeChoiceManager extends BaseHolidayManager {

    private int mRangeSize;

    public RangeChoiceManager(int rangeSize) {
        mRangeSize = rangeSize;
    }

    @Override
    public void onClick(View cellView, CellBean cellBean) {
        List<CellBean> selectedCells = monthAdapter.getSelectedCell();
        int size = selectedCells.size();
        if (size == 0) {
            cellBean.isSelected = !cellBean.isSelected;
            monthAdapter.notifyDataSetChanged();
        } else if (size == 1) {
            CellBean selectedCell = selectedCells.get(0);
            Date selectedDate = selectedCell.date;
            Date date = cellBean.date;

            Date firstSelectedDate;
            Date lastSelectedDate;
            if (selectedDate.before(date)) {
                if(mRangeSize != -1 && !date.before(getDateLater(selectedDate, mRangeSize))) {
                    if(mOnClickRangeListener != null && mOnClickRangeListener.onBeyond()) {
                        return;
                    }
                }
                firstSelectedDate = selectedDate;
                lastSelectedDate = date;
            } else if(selectedDate.after(date)){
                selectedCell.isSelected = false;
                cellBean.isSelected = true;
                monthAdapter.notifyDataSetChanged();
                return;
            } else { // 再次点击取消选中
                selectedCell.isSelected = false;
                monthAdapter.notifyDataSetChanged();
                return;
            }
            for (int i = 0, count = monthAdapter.getItemCount(); i < count; i++) {
                BaseCellAdapter itemAdapter = monthAdapter.getItem(i);
                for (int j = 0, itemCount = itemAdapter.getCount(); j < itemCount; j++) {
                    CellBean tempCell = itemAdapter.getItem(j);
                    Date tempDate = tempCell.date;
                    if (!tempDate.before(firstSelectedDate) && !tempDate.after(lastSelectedDate)) {
                        tempCell.isSelected = true;
                    }
                }
            }
            monthAdapter.notifyDataSetChanged();
        } else {
            for (CellBean itemCell : selectedCells) {
                itemCell.isSelected = false;
            }
            cellBean.isSelected = true;
            monthAdapter.notifyDataSetChanged();
        }
    }

    private OnClickRangeListener mOnClickRangeListener;

    public void setOnClickRangeListener(OnClickRangeListener onClickRangeListener) {
        mOnClickRangeListener = onClickRangeListener;
    }

    public interface OnClickRangeListener {
        boolean onBeyond();
    }
}
