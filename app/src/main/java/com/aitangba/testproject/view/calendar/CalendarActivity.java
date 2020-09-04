package com.aitangba.testproject.view.calendar;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aitangba.testproject.R;
import com.aitangba.testproject.view.calendar.common.manager.RangeChoiceManager;
import com.aitangba.testproject.view.calendar.common.view.CalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by fhf11991 on 2017/4/11.
 */

public class CalendarActivity extends AppCompatActivity {

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
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


        Date fromDate = new Date();

        Calendar toDateCal = Calendar.getInstance();
        toDateCal.add(Calendar.DATE, 60);
        Date toDate = toDateCal.getTime();

        CalendarView calendarView = findViewById(R.id.calendarView);
        RangeChoiceManager manager = new RangeChoiceManager(5);
        manager.setCellSelectableFilter(new RangeChoiceManager.CellSelectableFilter() {
            @Override
            public boolean onBeyond() {
                return false;
            }
        });
        calendarView.init(fromDate, toDate).build(manager);
//        calendarView.init(fromDate, toDate).build(new MultipleChoiceManager(5));


//        calendarView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    List<String> flagDates = Arrays.asList(new String[]{"2018-06-07", "2018-06-09", "2018-06-18"});
//
//                    List<Date> list = new ArrayList<>();
//                    for(String dateStr : flagDates) {
//                        list.add(mSimpleDateFormat.parse(dateStr));
//                    }
//
//                    manager.setCornerFlags(list, "休");
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 2000);
//
//        calendarView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    List<String> flagDates = Arrays.asList(new String[]{"2018-06-07", "2018-06-09", "2018-06-18"});
//
//                    Map<Date, String> map = new HashMap<>();
//                    for(String dateStr : flagDates) {
//                        map.put(mSimpleDateFormat.parse(dateStr), "节假日");
//                    }
//
//                    manager.setHolidays(map);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 4000);
    }
}
