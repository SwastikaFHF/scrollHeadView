package com.aitangba.testproject.view.calendar.hotel;

import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aitangba.testproject.R;
import com.aitangba.testproject.databinding.ItemCalendarMonthBinding;
import com.aitangba.testproject.databinding.ItemCalendarWeekBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2017/4/11.
 */

public class HolidayCalendarAdapter extends BaseCalendarAdapter {

    private List<HolidayCalendarBean> mHolidays = new ArrayList<>();
    private List<HolidayCalendarBean> mRestDays = new ArrayList<>();

    public void setHolidays(List<HolidayCalendarBean> holidays) {
        mHolidays.addAll(holidays);
    }

    public void setRestDays(List<HolidayCalendarBean> restDays) {
        mRestDays.addAll(restDays);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == CalendarBean.MONTH) {
            ItemCalendarMonthBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_calendar_month, parent, false);
            return new RecyclerViewHolder(binding);
        } else {
            ItemCalendarWeekBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_calendar_week, null, false);
            return new RecyclerViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        final CalendarBean calendarBean = mList.get(position);
        if(holder.mBinding instanceof ItemCalendarMonthBinding) {
            ItemCalendarMonthBinding binding = (ItemCalendarMonthBinding) holder.mBinding;
            String month = "--------- " + calendarBean.year + "年" + (calendarBean.month + 1) + "月" + " ---------";
            binding.monthText.setText(month);

        } else if(holder.mBinding instanceof ItemCalendarWeekBinding) {
            ItemCalendarWeekBinding binding = (ItemCalendarWeekBinding) holder.mBinding;

            if(calendarBean.currentMonth) {
                binding.getRoot().setVisibility(View.VISIBLE);

                if(calendarBean.today) {
                    binding.dayText.setText("今天");
                } else if(calendarBean.firstSelectedDay) {
                    binding.dayText.setText("当前");
                } else {
                    binding.dayText.setText(String.valueOf(calendarBean.date));
                }

                matchHoliday(binding.dayText, calendarBean);

                binding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClick(calendarBean);
                    }
                });

                int rangType = getRangType(calendarBean);

                if(rangType == BaseCalendarAdapter.START || rangType == BaseCalendarAdapter.END) {
                    binding.getRoot().setBackgroundColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.colorPrimaryDark));
                } else if(rangType == BaseCalendarAdapter.RANGE) {
                    binding.getRoot().setBackgroundColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.colorPrimary));
                } else {
                    binding.getRoot().setBackgroundDrawable(null);
                }
            } else {
                binding.getRoot().setVisibility(View.GONE);
            }

        }
    }

    private void matchHoliday(TextView textView, CalendarBean calendarBean) {
        for(HolidayCalendarBean holidayCalendarBean : mHolidays) {
            if(holidayCalendarBean.sameDay(calendarBean)) {
                textView.setText(holidayCalendarBean.holidayName);
                break;
            }
        }
    }
}
