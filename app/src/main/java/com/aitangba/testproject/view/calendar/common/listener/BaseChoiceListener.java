package com.aitangba.testproject.view.calendar.common.listener;

import com.aitangba.testproject.view.calendar.common.CellAdapter;
import com.aitangba.testproject.view.calendar.common.MonthAdapter;

/**
 * Created by fhf11991 on 2018/3/28.
 */

public abstract class BaseChoiceListener implements CellAdapter.OnCellClickListener {

    private MonthAdapter mMonthAdapter;

    public MonthAdapter getMonthAdapter() {
        return mMonthAdapter;
    }

    public void attachMonthAdapter(MonthAdapter monthAdapter) {
        mMonthAdapter = monthAdapter;
    }
}
