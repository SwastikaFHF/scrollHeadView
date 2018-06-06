package com.aitangba.testproject.view.calendar.common.sample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aitangba.testproject.R;
import com.aitangba.testproject.view.calendar.common.BaseCellAdapter;
import com.aitangba.testproject.view.calendar.common.BaseCellManager;
import com.aitangba.testproject.view.calendar.common.CellBean;

/**
 * Created by fhf11991 on 2018/3/27.
 */

public class SampleCellAdapter extends BaseCellAdapter {

    private int mSpaceCount;

    private BaseCellManager mBaseCellManager;

    public SampleCellAdapter(int spaceCount) {
        mSpaceCount = spaceCount;
    }

    public void setBaseCellManager(BaseCellManager baseCellManager) {
        mBaseCellManager = baseCellManager;
    }

    @Override
    public int getSpaceCount() {
        return mSpaceCount;
    }

    @Override
    protected View onCreateView(LayoutInflater layoutInflater, ViewGroup parent) {
        return layoutInflater.inflate(R.layout.item_calendar_week, parent, false);
    }

    @Override
    protected void onBindView(View child, int position) {
        ViewHolder viewHolder = (ViewHolder) child.getTag(R.id.tag_holder);
        if(viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.title = child.findViewById(R.id.dayText);
            child.setTag(R.id.tag_holder, viewHolder);
        }

        CellBean cellBean = getItem(position);
        viewHolder.title.setEnabled(cellBean.enable);
        viewHolder.title.setChecked(cellBean.isSelected);
        if (cellBean.isToday) {
            viewHolder.title.setText("今天");
        } else {
            viewHolder.title.setText(String.valueOf(position + 1));
        }

        if(mBaseCellManager != null) {
            mBaseCellManager.onBind(child, cellBean);
        }
        viewHolder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBaseCellManager != null) {
                    mBaseCellManager.onClick(v, cellBean);
                }
            }
        });
    }

    public static class ViewHolder {
        private CheckableTextView title;
    }
}
