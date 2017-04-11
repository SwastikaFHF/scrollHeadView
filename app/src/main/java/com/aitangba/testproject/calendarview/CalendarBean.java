package com.aitangba.testproject.calendarview;

import java.util.Calendar;

/**
 * Created by fhf11991 on 2017/4/10.
 */

public class CalendarBean extends BaseCalendarBean {
    public final static int MONTH = 1;
    public final static int WEEK = 2;

    public int type; //1 month, 2 week
    public boolean currentMonth;
    public boolean today;
    public boolean firstSelectedDay;

    public CalendarBean(int type, Calendar calendar) {
        super(calendar);
        this.type = type;
    }
}
