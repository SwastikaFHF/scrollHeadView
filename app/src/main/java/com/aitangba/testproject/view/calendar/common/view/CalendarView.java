package com.aitangba.testproject.view.calendar.common.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

import com.aitangba.testproject.view.calendar.common.MonthAdapter;
import com.aitangba.testproject.view.calendar.common.pojo.CellBean;
import com.aitangba.testproject.view.calendar.common.manager.BaseHolidayManager;
import com.aitangba.testproject.view.calendar.common.pojo.WeekBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by fhf11991 on 2018/3/28.
 */

public class CalendarView extends RecyclerView {

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM");

    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public Builder init(Date fromDate, Date toDate) {
        return new Builder(fromDate, toDate);
    }

    private void validateAndUpdate(@NonNull Builder builder) {
        setHasFixedSize(true);
        setLayoutManager(new LinearLayoutManager(getContext()));
        MonthAdapter adapter = new MonthAdapter();
        adapter.setBaseCellManager(builder.baseCellManager);
        setAdapter(adapter);

        builder.baseCellManager.attachMonthAdapter(adapter);

        Calendar nowCalendar = Calendar.getInstance();
        clearTime(nowCalendar);

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(builder.startDate);
        clearTime(startCalendar);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(builder.endDate);
        clearTime(endCalendar);

        int disMonth = getMonth(startCalendar.getTime(), endCalendar.getTime()) + 1;

        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.setTime(builder.startDate);
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1); // the first day of start month
        clearTime(tempCalendar);

        List<WeekBean> list = new ArrayList<>();
        CellBean cellBean;

        for (int i = 0; i < disMonth; i++) {
            int maxDay = tempCalendar.getActualMaximum(Calendar.DATE); // the max day of current month
            final int weekIndex = Math.max(0, tempCalendar.get(Calendar.DAY_OF_WEEK) - 1);
            Log.d("Calendar", "weekIndex = " + weekIndex);
            WeekBean weekBean;
            int weekCount = (int) Math.ceil((maxDay + weekIndex) / 7d);

            int index = 0;
            retry:
            for (int j = 0; j < weekCount; j++) {
                weekBean = new WeekBean();
                if(j == 0) {
                    weekBean.title = mSimpleDateFormat.format(tempCalendar.getTime());
                    weekBean.spacingColumn = weekIndex;
                }

                for (;index < maxDay;) {
                    cellBean = new CellBean();
                    cellBean.index = index + 1;
                    cellBean.isToday = nowCalendar.equals(tempCalendar);
                    int weekDay = tempCalendar.get(Calendar.DAY_OF_WEEK);
                    cellBean.isWeekend = weekDay == Calendar.SATURDAY || weekDay == Calendar.SUNDAY;
                    cellBean.enable = !tempCalendar.before(startCalendar) && !tempCalendar.after(endCalendar);
                    cellBean.date = tempCalendar.getTime();
                    weekBean.cellBeans.add(cellBean);
                    tempCalendar.add(Calendar.DAY_OF_MONTH, 1); // next day of current month or the first day of next month

                    index = index + 1;
                    if((index + weekIndex) % 7 == 0 || index == maxDay) {
                        list.add(weekBean);
                        continue retry;
                    }
                }
            }
        }

        adapter.setData(list);
    }

    private void clearTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private int getMonth(Date start, Date end) {
        if (start.after(end)) {
            Date t = start;
            start = end;
            end = t;
        }
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(start);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(end);
        Calendar temp = Calendar.getInstance();
        temp.setTime(end);
        temp.add(Calendar.DATE, 1);

        int year = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int month = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);

        if ((startCalendar.get(Calendar.DATE) == 1) && (temp.get(Calendar.DATE) == 1)) {
            return year * 12 + month + 1;
        } else if ((startCalendar.get(Calendar.DATE) != 1) && (temp.get(Calendar.DATE) == 1)) {
            return year * 12 + month;
        } else if ((startCalendar.get(Calendar.DATE) == 1) && (temp.get(Calendar.DATE) != 1)) {
            return year * 12 + month;
        } else {
            return (year * 12 + month - 1) < 0 ? 0 : (year * 12 + month);
        }
    }

    public class Builder {
        private Date startDate;
        private Date endDate;
        private List<Date> selectedDates = new ArrayList<>();
        private BaseHolidayManager baseCellManager;

        Builder(Date startDate, Date endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public Builder withSelectedDates(List<Date> selectedDates) {
            this.selectedDates.clear();
            this.selectedDates.addAll(selectedDates);
            return this;
        }

        public void build(@NonNull BaseHolidayManager baseCellManager) {
            this.baseCellManager = baseCellManager;
            validateAndUpdate(this);
        }
    }
}


