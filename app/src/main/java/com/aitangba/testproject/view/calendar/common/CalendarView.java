package com.aitangba.testproject.view.calendar.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.aitangba.testproject.view.calendar.common.listener.BaseChoiceListener;
import com.aitangba.testproject.view.calendar.common.listener.CustomChoiceListener;
import com.aitangba.testproject.view.calendar.common.listener.MultipleChoiceListener;
import com.aitangba.testproject.view.calendar.common.listener.RangeChoiceListener;
import com.aitangba.testproject.view.calendar.common.listener.SingleChoiceListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by fhf11991 on 2018/3/28.
 */

public class CalendarView extends RecyclerView {

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM");
    private final List<BaseChoiceListener> mListeners = new ArrayList<>();

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

    public void addListener(BaseChoiceListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(BaseChoiceListener listener) {
        mListeners.remove(listener);
    }

    private void validateAndUpdate(@NonNull Builder builder) {
        setLayoutManager(new LinearLayoutManager(getContext()));
        MonthAdapter adapter = new MonthAdapter();
        setAdapter(adapter);

        BaseChoiceListener onCellClickListener = null;
        switch (builder.mode) {
            case SINGLE:
                onCellClickListener = new SingleChoiceListener();
                break;
            case RANGE:
                onCellClickListener = new RangeChoiceListener(builder.multipleSize);
                break;
            case MULTIPLE:
                onCellClickListener = new MultipleChoiceListener(builder.multipleSize);
                break;
            case CUSTOM:
                onCellClickListener = new CustomChoiceListener(mListeners);
                break;
        }
        onCellClickListener.attachMonthAdapter(adapter);

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

        List<CellAdapter> list = new ArrayList<>();
        CellBean cellBean;

        for (int i = 0; i < disMonth; i++) {
            int maxDay = tempCalendar.getActualMaximum(Calendar.DATE); // the max day of current month

            final int weekIndex = Math.max(0, tempCalendar.get(Calendar.DAY_OF_WEEK) - 1);
            final List<CellBean> cellBeanList = new ArrayList<>();
            for (int j = 0; j < maxDay; j++) {
                cellBean = new CellBean();
                cellBean.isToday = nowCalendar.equals(tempCalendar);
                cellBean.enable = !tempCalendar.before(startCalendar) && !tempCalendar.after(endCalendar);
                cellBean.date = tempCalendar.getTime();
                cellBeanList.add(cellBean);
                tempCalendar.add(Calendar.DAY_OF_MONTH, 1); // next day of current month or the first day of next month
            }
            CellAdapter cellAdapter = new CellAdapter(weekIndex, cellBeanList, mSimpleDateFormat.format(cellBeanList.get(0).date));
            cellAdapter.setOnCellClickListener(onCellClickListener);
            list.add(cellAdapter);
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
        public String flagsText;
        public List<String> flagDates;
        public Map<String, String> holidayMap; // 2018-04-01, 清明节
        public Date startDate;
        public Date endDate;
        public List<Date> selectedDates = new ArrayList<>();
        public SelectionMode mode;
        public int multipleSize = -1;

        public Builder(Date startDate, Date endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public Builder setCornerFlags(List<String> flagDates, String flagsText) {
            this.flagDates = flagDates;
            this.flagsText = flagsText;
            return this;
        }

        public Builder setHolidays(Map<String, String> holidayMap) {
            this.holidayMap = holidayMap;
            return this;
        }

        public Builder setMultipleSize(int multipleSize) {
            this.multipleSize = multipleSize;
            return this;
        }

        public void build(SelectionMode selectionMode) {
            this.mode = selectionMode;
            validateAndUpdate(this);
        }
    }

    public enum SelectionMode {
        SINGLE,MULTIPLE,RANGE,CUSTOM
    }
}


