package com.aitangba.testproject.view.calendar.common;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2018/6/5
 */
public abstract class BaseCellAdapter {
    private List<CellBean> mList = new ArrayList<>();
    private String mTitle;

    protected abstract View onCreateView(LayoutInflater layoutInflater, ViewGroup parent);

    protected abstract void onBindView(View child, int position);

    public abstract int getSpaceCount();

    public int getCount() {
        return mList.size();
    }

    public void setData(@NonNull List<CellBean> list) {
        mList.clear();
        mList.addAll(list);
    }

    public CellBean getItem(int position) {
        return mList.get(position);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
