package com.aitangba.testproject.view.calendar.hotel;

import java.util.Calendar;

/**
 * Created by fhf11991 on 2017/4/11.
 */

public class BaseCalendarBean {

    public int year;
    public int month;
    public int date;

    public BaseCalendarBean() {
    }

    public BaseCalendarBean(Calendar calendar) {
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.date = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public boolean before(BaseCalendarBean calendarBean) {
        int current = year * 10000 + month * 100 + date;
        int calendar = calendarBean.year * 10000 + calendarBean.month * 100 + calendarBean.date;
        return current < calendar;
    }

    public boolean after(BaseCalendarBean calendarBean) {
        int current = year * 10000 + month * 100 + date;
        int calendar = calendarBean.year * 10000 + calendarBean.month * 100 + calendarBean.date;
        return current > calendar;
    }

    public boolean sameDay(BaseCalendarBean calendarBean) {
        int current = year * 10000 + month * 100 + date;
        int calendar = calendarBean.year * 10000 + calendarBean.month * 100 + calendarBean.date;
        return current == calendar;
    }
}
