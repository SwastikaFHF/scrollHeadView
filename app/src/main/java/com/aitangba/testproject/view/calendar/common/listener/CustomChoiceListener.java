package com.aitangba.testproject.view.calendar.common.listener;

import android.view.View;

import com.aitangba.testproject.view.calendar.common.CellAdapter;
import com.aitangba.testproject.view.calendar.common.CellBean;
import com.aitangba.testproject.view.calendar.common.MonthAdapter;

import java.util.List;

/**
 * Created by fhf11991 on 2018/3/28.
 */

public class CustomChoiceListener extends BaseChoiceListener {

    private final List<BaseChoiceListener> mListeners;

    public CustomChoiceListener(List<BaseChoiceListener> listeners) {
        mListeners = listeners;
    }

    @Override
    public void onClick(CellAdapter cellAdapter, View cellView, CellBean cellBean) {
        for(BaseChoiceListener listener : mListeners) {
            listener.onClick(cellAdapter, cellView, cellBean);
        }
    }

    @Override
    public void attachMonthAdapter(MonthAdapter monthAdapter) {
        super.attachMonthAdapter(monthAdapter);
        for(BaseChoiceListener listener : mListeners) {
            listener.attachMonthAdapter(monthAdapter);
        }
    }
}
