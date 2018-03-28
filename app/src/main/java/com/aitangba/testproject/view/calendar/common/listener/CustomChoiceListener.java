package com.aitangba.testproject.view.calendar.common.listener;

import android.view.View;

import com.aitangba.testproject.view.calendar.common.CellAdapter;
import com.aitangba.testproject.view.calendar.common.CellBean;
import com.aitangba.testproject.view.calendar.common.MonthAdapter;

/**
 * Created by fhf11991 on 2018/3/28.
 */

public class CustomChoiceListener implements CellAdapter.OnCellClickListener{
    private MonthAdapter mMonthAdapter;

    public CustomChoiceListener(MonthAdapter monthAdapter) {
        mMonthAdapter = monthAdapter;
    }

    @Override
    public void onClick(CellAdapter cellAdapter, View cellView, CellBean cellBean) {

    }
}
