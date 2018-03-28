package com.aitangba.testproject.view.calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.aitangba.testproject.R;
import com.aitangba.testproject.view.calendar.common.CalendarView;
import com.aitangba.testproject.view.calendar.common.MonthAdapter;

import java.util.Calendar;
import java.util.Date;

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

        Calendar toDateCal = Calendar.getInstance();
        toDateCal.add(Calendar.DATE, 20);

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.init(new Date(), toDateCal.getTime()).setMultipleSize(10).build(CalendarView.SelectionMode.MULTIPLE);

    }
}
