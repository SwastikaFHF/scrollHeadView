package com.aitangba.testproject.verticalscroll.listview;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aitangba.testproject.R;
import com.aitangba.testproject.verticalscroll.ScrollHeadView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2016/6/7.
 */
public class ListViewActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
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

        Adapter adapter = new Adapter(this);
        ListView recyclerView = (ListView) findViewById(R.id.listView);
        recyclerView.setAdapter(adapter);
        adapter.setData(getData(40));

        TextView textView = new TextView(this);
        textView.setText("测试头部");
        ScrollHeadView scrollHeadView = (ScrollHeadView) findViewById(R.id.scrollView);
        scrollHeadView.setHeadView(textView);
    }

    private List<String> getData(int size){
        List<String> data = new ArrayList<>(size);
        for(int i = 0 ; i < size ; i++){
            data.add("测试 " + i);
        }
        return data;
    }
    private static class Adapter extends BaseAdapter {
        private List<String> mList;

        private Context mContext;
        public Adapter(Context context) {
            mContext = context;
        }

        public void setData(List<String> list) {
            mList = list;
            notifyDataSetChanged();
        }

        @Override
        public String getItem(int position) {
            return mList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null ) {
                convertView = new TextView(mContext);
            }
            ((TextView)convertView).setText(getItem(position));
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }
    }
}
