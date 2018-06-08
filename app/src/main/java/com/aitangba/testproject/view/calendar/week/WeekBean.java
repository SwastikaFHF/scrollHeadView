package com.aitangba.testproject.view.calendar.week;

import com.aitangba.testproject.view.calendar.CellBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2018/6/8
 */
public class WeekBean {
    public List<CellBean> cellBeans = new ArrayList<>(7);

    public int getSpacingColumn() {
        return 7 - cellBeans.size();
    }
    public String title;
}
