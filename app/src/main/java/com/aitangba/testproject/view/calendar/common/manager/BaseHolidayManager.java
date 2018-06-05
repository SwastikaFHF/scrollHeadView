package com.aitangba.testproject.view.calendar.common.manager;

import android.view.View;

import com.aitangba.testproject.view.calendar.common.CellBean;
import com.aitangba.testproject.view.calendar.common.celladapter.CellAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by fhf11991 on 2018/6/5
 */
public abstract class BaseHolidayManager implements BaseCellManager<CellAdapter> {

    protected String flagsText;
    protected List<String> flagDates;
    protected Map<String, String> holidayMap; // 2018-04-01, 清明节

    public BaseHolidayManager setCornerFlags(List<String> flagDates, String flagsText) {
        this.flagDates = flagDates;
        this.flagsText = flagsText;
        return this;
    }

    public BaseHolidayManager setHolidays(Map<String, String> holidayMap) {
        this.holidayMap = holidayMap;
        return this;
    }

    @Override
    public void onBind(View cellView, CellBean cellBean) {

    }
}
