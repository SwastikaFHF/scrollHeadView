package com.aitangba.testproject.view.calendar.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aitangba.testproject.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2018/3/27.
 */

public class CellAdapter extends MonthView.BaseCellAdapter {

    private int mSpaceCount;
    private List<CellBean> mList = new ArrayList<>();
    private String mTitle;

    public CellAdapter(int spaceCount, List<CellBean> list, String title) {
        mSpaceCount = spaceCount;
        mList.clear();
        mList.addAll(list);
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public int getSpaceCount() {
        return mSpaceCount;
    }

    @Override
    protected View onCreateView(LayoutInflater layoutInflater, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.item_calendar_week, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.title = view.findViewById(R.id.dayText);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    protected void onBindView(View child, int position) {
        ViewHolder viewHolder = (ViewHolder) child.getTag();
        if (viewHolder != null) {
            CellBean cellBean = mList.get(position);
            viewHolder.title.setEnabled(cellBean.enable);
            viewHolder.title.setChecked(cellBean.isSelected);
            if (cellBean.isToday) {
                viewHolder.title.setText("今天");
            } else {
                viewHolder.title.setText(String.valueOf(position + 1));
            }

            viewHolder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cellBean.isSelected = !cellBean.isSelected;
                    notifyDataSetChanged();
                }
            });
        }
    }

    public static class ViewHolder {
        private CheckableTextView title;
    }
}
