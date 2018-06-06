package com.aitangba.testproject.view.calendar.common;

import android.view.View;

/**
 * Created by fhf11991 on 2018/6/5
 */
public interface BaseCellManager {

    void attachMonthAdapter(MonthAdapter monthAdapter);

    void onBind(View cellView, CellBean cellBean);

    void onClick(View cellView, CellBean cellBean);
}
