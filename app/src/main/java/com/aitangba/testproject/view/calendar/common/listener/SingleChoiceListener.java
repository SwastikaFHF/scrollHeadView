package com.aitangba.testproject.view.calendar.common.listener;

import android.view.View;

import com.aitangba.testproject.view.calendar.common.CellAdapter;
import com.aitangba.testproject.view.calendar.common.CellBean;

/**
 * Created by fhf11991 on 2018/3/27.
 */

public class SingleChoiceListener  extends BaseChoiceListener {

    @Override
    public void onClick(CellAdapter cellAdapter, View cellView, CellBean cellBean) {
        for (int i = 0, count = getMonthAdapter().getItemCount(); i < count; i++) {
            CellAdapter itemAdapter = getMonthAdapter().getItem(i);
            for (int j = 0, itemCount = itemAdapter.getCount(); j < itemCount; j++) {
                itemAdapter.getItem(j).isSelected = false;
            }
        }
        cellBean.isSelected = !cellBean.isSelected;
        getMonthAdapter().notifyDataSetChanged();
    }
}
