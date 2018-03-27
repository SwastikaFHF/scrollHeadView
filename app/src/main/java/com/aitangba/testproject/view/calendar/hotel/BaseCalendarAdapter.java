package com.aitangba.testproject.view.calendar.hotel;

import android.databinding.ViewDataBinding;
import android.support.annotation.IntDef;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2017/4/11.
 */

public abstract class BaseCalendarAdapter extends RecyclerView.Adapter<BaseCalendarAdapter.RecyclerViewHolder> {

    public final static int MODE_SINGLE = 1;
    public final static int MODE_RANGE = 2;

    @IntDef({MODE_SINGLE, MODE_RANGE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SelectionMode {}

    private int type = MODE_RANGE;

    private CalendarBean selectedCalendarBean;
    private CalendarBean lastCalendarBean;

    public void setType(@SelectionMode int type) {
        this.type = type;
    }

    protected List<CalendarBean> mList = new ArrayList<>();

    public void setData(List<CalendarBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        CalendarBean calendarBean = mList.get(position);
        return calendarBean.type;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected void onItemClick(final CalendarBean calendarBean) {
        if(type == MODE_SINGLE) {
            if(selectedCalendarBean == calendarBean) {
                //do nothing
            } else {
                selectedCalendarBean = calendarBean;
            }
            notifyDataSetChanged();
        } else {
            if(selectedCalendarBean == null) {
                selectedCalendarBean = calendarBean;
            } else if(selectedCalendarBean == calendarBean) {
                //do nothing
            } else {
                if(selectedCalendarBean.after(calendarBean)) {
                    CalendarBean temp = selectedCalendarBean;
                    selectedCalendarBean = calendarBean;
                    lastCalendarBean = temp;
                } else {
                    lastCalendarBean = calendarBean;
                }
            }
            notifyDataSetChanged();
        }
    }

    public @RangType int getRangType(final CalendarBean calendarBean) {
        if(selectedCalendarBean == null) {
            return NONE;
        }

        if(calendarBean == selectedCalendarBean) {
            return START;
        }

        if(lastCalendarBean == null) {
            return NONE;
        }

        if(calendarBean == lastCalendarBean){
            return END;
        }

        if(calendarBean.after(selectedCalendarBean) && calendarBean.before(lastCalendarBean)) {
            return RANGE;
        } else {
            return NONE;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if(recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if(getItemViewType(position) == CalendarBean.MONTH) {
                        return gridLayoutManager.getSpanCount();
                    }
                    return 1;
                }
            });
        }
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        public ViewDataBinding mBinding;

        public RecyclerViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NONE, START, END, RANGE})
    public @interface RangType{}
    public final static int NONE = 0;
    public final static int START = 1;
    public final static int END = 2;
    public final static int RANGE = 3;

}
