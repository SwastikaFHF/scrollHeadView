package com.aitangba.testproject.view.calendar.common;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.aitangba.testproject.R;
import com.aitangba.testproject.databinding.ItemCalendarCommonBinding;
import com.aitangba.testproject.view.RecyclerViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by fhf11991 on 2018/3/27.
 */

public class CommonAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private List<CellAdapter> mList = new ArrayList();
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM");

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_calendar_common, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        ItemCalendarCommonBinding binding = holder.getBing();
        CellAdapter adapter = mList.get(position);
        binding.titleText.setText(adapter.getTitle());
        binding.monthView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setData(Date startDate, int rang) {
        Calendar nowCalendar = Calendar.getInstance();
        clearTime(nowCalendar);

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        clearTime(startCalendar);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(startDate);
        endCalendar.add(Calendar.DATE, rang);
        clearTime(endCalendar);

        int disMonth = getMonth(startCalendar.getTime(), endCalendar.getTime()) + 1;

        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.setTime(startDate);
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
            list.add(new CellAdapter(weekIndex, cellBeanList, mSimpleDateFormat.format(cellBeanList.get(0).date)));
        }

        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
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

        if ((startCalendar.get(Calendar.DATE) == 1)&& (temp.get(Calendar.DATE) == 1)) {
            return year * 12 + month + 1;
        } else if ((startCalendar.get(Calendar.DATE) != 1) && (temp.get(Calendar.DATE) == 1)) {
            return year * 12 + month;
        } else if ((startCalendar.get(Calendar.DATE) == 1) && (temp.get(Calendar.DATE) != 1)) {
            return year * 12 + month;
        } else {
            return (year * 12 + month - 1) < 0 ? 0 : (year * 12 + month);
        }
    }
}
