package com.aitangba.testproject.view.calendar.common.manager;

import android.util.Log;

import com.aitangba.testproject.view.calendar.CellBean;
import com.aitangba.testproject.view.calendar.week.MonthAdapter;
import com.aitangba.testproject.view.calendar.week.WeekBean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by fhf11991 on 2018/6/5
 */
public abstract class BaseHolidayManager implements BaseCellManager {

    protected MonthAdapter monthAdapter;
    private static final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public BaseHolidayManager setCornerFlags(List<Date> flagDates, String flagsText) {
        Collections.sort(flagDates);

        retry:
        for (int i = 0, count = monthAdapter.getItemCount(); i < count; i++) {
            WeekBean item = monthAdapter.getItem(i);
            for (int j = 0, itemCount = item.cellBeans.size(); j < itemCount; j++) {
                if(flagDates.size() == 0) {
                    break retry;
                }
                CellBean cellBean = item.cellBeans.get(j);
                Date date = flagDates.get(0);
                if(date.compareTo(cellBean.date) == 0) {
                    cellBean.flag = flagsText;
                    flagDates.remove(date);
                }
            }
        }
        monthAdapter.notifyDataSetChanged();
        return this;
    }

    public BaseHolidayManager setHolidays(Map<Date, String> holidayMap) {
        Map<Date, String> sortMap = new TreeMap<>(new MapKeyComparator());
        sortMap.putAll(holidayMap);

        int mapIndex = 0;
        int mapSize = sortMap.size();
        retry:
        for (int i = 0, count = monthAdapter.getItemCount(); i < count; i++) {
            WeekBean item = monthAdapter.getItem(i);
            for (int j = 0, itemCount = item.cellBeans.size(); j < itemCount; j++) {
                if(mapIndex == mapSize) {
                    break retry;
                }
                CellBean cellBean = item.cellBeans.get(j);
                Log.d("CellManager" ,"cellBean = " + mSimpleDateFormat.format(cellBean.date));

                String value = sortMap.get(cellBean.date);
                if(value != null) {
                    cellBean.holiday = value;
                    mapIndex = mapIndex + 1;
                }
            }
        }
        monthAdapter.notifyDataSetChanged();
        return this;
    }

    @Override
    public void attachMonthAdapter(MonthAdapter monthAdapter) {
        this.monthAdapter = monthAdapter;
    }

    static Date getDateLater(Date date, int dateNum) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, dateNum);
        return cal.getTime();
    }

    private static class MapKeyComparator implements Comparator<Date>{

        @Override
        public int compare(Date date1, Date date2) {
            return date1.compareTo(date2);
        }
    }
}
