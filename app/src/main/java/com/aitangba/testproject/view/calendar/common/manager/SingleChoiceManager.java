package com.aitangba.testproject.view.calendar.common.manager;

import android.view.View;

import com.aitangba.testproject.view.calendar.common.pojo.CellBean;
import com.aitangba.testproject.view.calendar.common.pojo.WeekBean;

/**
 * Created by fhf11991 on 2018/6/5
 */
public class SingleChoiceManager extends BaseHolidayManager {

    @Override
    public void onClick(View cellView, CellBean cellBean) {
        for (int i = 0, count = monthAdapter.getItemCount(); i < count; i++) {
            WeekBean item = monthAdapter.getItem(i);
            for (int j = 0, itemCount = item.cellBeans.size(); j < itemCount; j++) {
                item.cellBeans.get(j).isSelected = false;
            }
        }
        cellBean.isSelected = !cellBean.isSelected;
        monthAdapter.notifyDataSetChanged();
    }
}
