package com.aitangba.testproject.view.calendar.common.manager;

import android.view.View;

import com.aitangba.testproject.view.calendar.common.BaseCellAdapter;
import com.aitangba.testproject.view.calendar.common.celladapter.CellAdapter;
import com.aitangba.testproject.view.calendar.common.CellBean;
import com.aitangba.testproject.view.calendar.common.MonthAdapter;

/**
 * Created by fhf11991 on 2018/6/5
 */
public class SingleChoiceManager extends BaseHolidayManager {

    @Override
    public void onClick(MonthAdapter monthAdapter, CellAdapter cellAdapter, View cellView, CellBean cellBean) {
        for (int i = 0, count = monthAdapter.getItemCount(); i < count; i++) {
            BaseCellAdapter itemAdapter = monthAdapter.getItem(i);
            for (int j = 0, itemCount = itemAdapter.getCount(); j < itemCount; j++) {
                itemAdapter.getItem(j).isSelected = false;
            }
        }
        cellBean.isSelected = !cellBean.isSelected;
        monthAdapter.notifyDataSetChanged();
    }
}
