package com.aitangba.testproject.view.calendar.hotel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by fhf11991 on 2017/4/11.
 */

public class CalendarUtils {

    public static List<CalendarBean> getData(Date selectedDate, int rang) {
        final Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.setTime(selectedDate);
        selectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
        selectedCalendar.set(Calendar.MINUTE, 0);
        selectedCalendar.set(Calendar.SECOND, 0);
        selectedCalendar.set(Calendar.MILLISECOND, 0);

        final Calendar nowCalendar = Calendar.getInstance();
        final int year = nowCalendar.get(Calendar.YEAR);
        final int month = nowCalendar.get(Calendar.MONTH);
        final int date = nowCalendar.get(Calendar.DAY_OF_MONTH);

        Calendar firstCalendar = Calendar.getInstance();
        firstCalendar.set(year, month, date, 0, 0, 0);
        firstCalendar.set(Calendar.MILLISECOND, 0);

        Calendar lastCalendar = Calendar.getInstance();
        lastCalendar.set(year, month, date, 0, 0, 0);
        lastCalendar.set(Calendar.MILLISECOND, 0);
        lastCalendar.add(Calendar.DAY_OF_MONTH, rang);

        int monthSpace = getMonthSpace(firstCalendar, lastCalendar) + 1;

        List<CalendarBean> list = new ArrayList<>();
        CalendarBean calendarBean;

        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(year, month, 1, 0, 0, 0);
        tempCalendar.set(Calendar.MILLISECOND, 0);
        for(int i = 0; i < monthSpace; i++) {
            final int currentMonth = tempCalendar.get(Calendar.MONTH);
            final int firstDayInWeek = tempCalendar.get(Calendar.DAY_OF_WEEK);
            final int headerDiffDays = firstDayInWeek - 1;
            final int diffDays = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            final int weekCount = (int) Math.ceil((double) (headerDiffDays + diffDays) / 7);

            calendarBean = new CalendarBean(CalendarBean.MONTH, tempCalendar);
            list.add(calendarBean);

            tempCalendar.add(Calendar.DAY_OF_MONTH, -headerDiffDays);
            for(int j = 0; j < weekCount; j ++) {
                for(int m = 0; m < 7 ;m ++) {
                    int tempMonth = tempCalendar.get(Calendar.MONTH);
                    calendarBean = new CalendarBean(CalendarBean.WEEK, tempCalendar);
                    calendarBean.currentMonth = currentMonth == tempMonth;
                    calendarBean.today = isSameDay(nowCalendar, tempCalendar);
                    calendarBean.firstSelectedDay = isSameDay(selectedCalendar, tempCalendar);

                    list.add(calendarBean);
                    tempCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }
            }

            tempCalendar.set(year, month + (i + 1), 1, 0, 0, 0);
        }
        return list;
    }

    private static boolean isSameDay(Calendar bef, Calendar aft) {
        if(bef.get(Calendar.YEAR) != aft.get(Calendar.YEAR)) {
            return false;
        } else if(bef.get(Calendar.MONTH) != aft.get(Calendar.MONTH)) {
            return false;
        } else if(bef.get(Calendar.DAY_OF_MONTH) != aft.get(Calendar.DAY_OF_MONTH)) {
            return false;
        }
        return true;
    }

    private static int getMonthSpace(Calendar bef, Calendar aft) {
        int result = aft.get(Calendar.MONTH) - bef.get(Calendar.MONTH);
        int month = (aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR)) * 12;
        return Math.abs(month + result);
    }
}
