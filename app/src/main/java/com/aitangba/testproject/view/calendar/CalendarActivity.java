package com.aitangba.testproject.view.calendar;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aitangba.testproject.R;
import com.aitangba.testproject.databinding.ItemCalendarMonthBinding;
import com.aitangba.testproject.databinding.ItemCalendarWeekBinding;
import com.aitangba.testproject.view.calendar.common.CellAdapter;
import com.aitangba.testproject.view.calendar.common.CellBean;
import com.aitangba.testproject.view.calendar.common.CommonAdapter;
import com.aitangba.testproject.view.calendar.hotel.CalendarUtils;
import com.aitangba.testproject.view.calendar.hotel.HolidayCalendarAdapter;
import com.aitangba.testproject.view.calendar.hotel.HolidayCalendarBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by fhf11991 on 2017/4/11.
 */

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

//        recyclerView.setLayoutManager(new GridLayoutManager(this, 7));
//        HolidayCalendarAdapter mAdapter = new HolidayCalendarAdapter();
//        recyclerView.setAdapter(mAdapter);
//
//        mAdapter.setData(CalendarUtils.getData(new Date(), 60));
//        List<HolidayCalendarBean> holidays = new ArrayList<>();
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DAY_OF_MONTH, 1);
//        HolidayCalendarBean holidayCalendarBean = new HolidayCalendarBean(calendar);
//        holidayCalendarBean.holidayName = "清明节";
//        holidays.add(holidayCalendarBean);
//
//        mAdapter.setHolidays(holidays);
//        mAdapter.notifyDataSetChanged();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CommonAdapter adapter;
        recyclerView.setAdapter(adapter = new CommonAdapter());

        adapter.setData(new Date(), 10);
    }
}
