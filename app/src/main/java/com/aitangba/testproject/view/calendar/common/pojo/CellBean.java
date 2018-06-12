package com.aitangba.testproject.view.calendar.common.pojo;

import java.util.Date;

/**
 * Created by fhf11991 on 2018/3/27.
 */

public class CellBean {
    public int index;
    public boolean isToday;
    public boolean isSelected;
    public Date date;
    public boolean enable;
    public boolean isWeekend;

    public String flag;
    public String holiday;

    public int option = OPTION_NONE; // 当前状态

    public static final int OPTION_NONE = 0;
    public static final int OPTION_FIRST = 1;
    public static final int OPTION_MIDDLE = 2;
    public static final int OPTION_LAST = 3;
}
