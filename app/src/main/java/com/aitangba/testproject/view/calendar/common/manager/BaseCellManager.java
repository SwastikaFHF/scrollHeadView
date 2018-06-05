package com.aitangba.testproject.view.calendar.common.manager;

import android.view.View;

import com.aitangba.testproject.view.calendar.common.BaseCellAdapter;
import com.aitangba.testproject.view.calendar.common.CellBean;
import com.aitangba.testproject.view.calendar.common.MonthAdapter;

/**
 * Created by fhf11991 on 2018/6/5
 */
public interface BaseCellManager<T extends BaseCellAdapter> {

    void onBind(View cellView, CellBean cellBean);

    void onClick(MonthAdapter monthAdapter, T cellAdapter, View cellView, CellBean cellBean);
}
