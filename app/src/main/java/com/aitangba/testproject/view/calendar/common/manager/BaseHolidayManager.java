package com.aitangba.testproject.view.calendar.common.manager;

import com.aitangba.testproject.view.calendar.common.BaseCellAdapter;
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

    protected Map<String, String> holidayMap; // 2018-04-01, 清明节

    public BaseHolidayManager setCornerFlags(List<Date> flagDates, String flagsText) {
        retry:
        for(Date date : flagDates) {
            for (int i = 0, count = monthAdapter.getItemCount(); i < count; i++) {
                BaseCellAdapter itemAdapter = monthAdapter.getItem(i);
                for (int j = 0, itemCount = itemAdapter.getCount(); j < itemCount; j++) {
                    CellBean cellBean = itemAdapter.getItem(j);
                    if(date.compareTo(cellBean.date) == 0) {
                        cellBean.flag = flagsText;
                        continue retry;
                    }
                }
            }
        }
        monthAdapter.notifyDataSetChanged();
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


    protected static Date getDateLater(Date date, int dateNum) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, dateNum);
        return cal.getTime();
    }
}
