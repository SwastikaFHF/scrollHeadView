package com.aitangba.testproject.paging;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aitangba.testproject.R;
import com.aitangba.testproject.paging.recyclerview.EasyRecyclerView;
import com.aitangba.testproject.paging.view.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2017/5/11.
 */

public class PagingRecyclerViewActivity extends AppCompatActivity {

    private Adapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paging_recycler_view);
        EasyRecyclerView recyclerView = (EasyRecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(boolean isReload) {
                Log.d("TAG", "onLoadMore ---");
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(mAdapter = new Adapter());
        mAdapter.setData(getData(40));
    }

    private List<String> getData(int size) {
        List<String> list = new ArrayList<>(size);
        for(int i= 0; i< size ; i++) {
            list.add( "名字" + i);
        }
        return list;
    }

    static class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private List<String> mList = new ArrayList<>();

        private int position;
        public void setData(List<String> items) {
            mList.addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d("Adapter", "-- onCreateViewHolder  position = " + position ++);
            View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_light_adapter, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
