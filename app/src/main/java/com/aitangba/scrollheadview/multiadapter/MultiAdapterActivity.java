package com.aitangba.scrollheadview.multiadapter;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aitangba.scrollheadview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2016/6/27.
 */
public class MultiAdapterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_adapter);
        final MultiAdapter adapter = new Adapter();
        adapter.setEmptyView(new EmptyView(this));
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setData(getData(50));
        adapter.setHeadViewSize(1);
        adapter.setAutoLoadMore(true);
        adapter.setEmptyView(R.layout.view_empty);


        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setData(getData(0));
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        adapter.setOnLoadMoreListener(new MultiAdapter.OnLoadMoreListener() {
            @Override
            public void onLoad() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addToFoot(getData(10));
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    private List<String> getData(int size){
        List<String> data = new ArrayList<>(size);
        for(int i = 0 ; i < size ; i++){
            data.add("测试 " + i);
        }
        return data;
    }

    private static class Adapter extends MultiAdapter<String>{

        @Override
        public int getCommonViewType(int position) {
            if(position == 1) {
                return TextViewHolder.TYPE;
            } else {
                return ViewHolder.TYPE;
            }
        }

        @Override
        public CommonViewHolder onCreateCommonViewHolder(ViewGroup parent, int viewType) {
            if(viewType == TextViewHolder.TYPE) {
                return new TextViewHolder(new TextView(parent.getContext()));
            } else {
                return new ViewHolder(new TextView(parent.getContext()));
            }
        }
    }

    private static class ViewHolder extends CommonViewHolder<String>{
        public static final int TYPE = 1;
        private TextView mItemView;
        private ViewHolder(TextView itemView) {
            super(itemView);
            mItemView = itemView;
        }



        @Override
        public void onBindViewHolder(String s) {
            mItemView.setText(s);
        }
    }

    private static class TextViewHolder extends CommonViewHolder<String>{
        public static final int TYPE = 2;
        private TextView mItemView;
        private TextViewHolder(TextView itemView) {
            super(itemView);
            mItemView = itemView;
        }

        @Override
        public void onBindViewHolder(String s) {
            mItemView.setText("我是假数据");
        }
    }
}
