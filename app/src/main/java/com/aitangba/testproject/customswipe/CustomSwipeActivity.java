package com.aitangba.testproject.customswipe;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aitangba.testproject.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2016/6/22.
 */
public class CustomSwipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_swipe);
        final CustomSwipeRefreshLayout swipeRefreshLayout = (CustomSwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        swipeRefreshLayout.setSwipeableChildren(R.id.llt_head, R.id.recyclerView);
        swipeRefreshLayout.setHeadViewId(R.id.llt_head);

        Adapter adapter = new Adapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setData(getData(80));
    }

    private List<String> getData(int size){
        List<String> data = new ArrayList<>(size);
        for(int i = 0 ; i < size ; i++){
            data.add("测试 " + i);
        }
        return data;
    }

    private static class Adapter extends RecyclerView.Adapter {
        private List<String> mList;
        public void setData(List<String> list) {
            mList = list;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(new TextView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((TextView)holder.itemView).setText(mList.get(holder.getAdapterPosition()));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
