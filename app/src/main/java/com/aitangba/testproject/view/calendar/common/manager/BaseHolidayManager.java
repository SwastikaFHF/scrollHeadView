package com.aitangba.testproject.view.calendar.common.manager;

import android.view.View;

import com.aitangba.testproject.view.calendar.common.BaseCellManager;
import com.aitangba.testproject.view.calendar.common.CellBean;
import com.aitangba.testproject.view.calendar.common.MonthAdapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by fhf11991 on 2018/6/5
 */
public abstract class BaseHolidayManager implements BaseCellManager {

    protected MonthAdapter monthAdapter;

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
    public void attachMonthAdapter(MonthAdapter monthAdapter) {
        this.monthAdapter = monthAdapter;
    }

    @Override
    public void onBind(View cellView, CellBean cellBean) {

    }

    protected static Date getDateLater(Date date, int dateNum) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, dateNum);
        return cal.getTime();
    }
}
