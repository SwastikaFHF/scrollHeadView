package com.aitangba.testproject.view.calendar.common.manager;

import android.view.View;

import com.aitangba.testproject.view.calendar.common.pojo.CellBean;
import com.aitangba.testproject.view.calendar.common.MonthAdapter;

/**
 * Created by fhf11991 on 2018/6/5
 */
public interface BaseCellManager {

    void attachMonthAdapter(MonthAdapter monthAdapter);

    void onClick(View cellView, CellBean cellBean);
}
