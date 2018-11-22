package com.aitangba.testproject.paging;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aitangba.testproject.R;
import com.aitangba.testproject.paging.effect.DialogEffectImpl;
import com.aitangba.testproject.paging.effect.PagingEffectImpl;
import com.aitangba.testproject.paging.effect.StatefulEffectImpl;
import com.aitangba.testproject.paging.effect.SwipeRefreshEffectImpl;
import com.aitangba.testproject.paging.helper.LoadingDialogHelper;
import com.aitangba.testproject.paging.helper.StatefulViewHelper;
import com.aitangba.testproject.paging.view.OnLoadMoreListener;
import com.aitangba.testproject.paging.view.PagingListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2017/3/30.
 */

public class PagingListViewActivity extends AppCompatActivity {

    private PagingListView mListView;
    private CustomAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paging_listview);

        mListView = (PagingListView) findViewById(R.id.listView);
        mListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadData(false);
            }
        });
        mAdapter = new CustomAdapter();
        mListView.setAdapter(mAdapter);

        loadData(true);
    }

    private List<String> getData(int size) {
        List<String> list = new ArrayList<>();
        for(int i = 0 ;i < size ; i ++) {
            list.add(" 数据 " + i);
        }
        return list;
    }

    private void loadData(final boolean refresh) {
        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> list = getData(10);
                mListView.checkPaging(list);
                mAdapter.addData(list, refresh);
            }
        }, 2000);
    }

    private static class CustomAdapter extends BaseAdapter {

        private List<String> mList = new ArrayList<>();

        public void addData(List<String> list, boolean refresh) {
            if(refresh) {
                mList.clear();
            }
            mList.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public String getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = new TextView(parent.getContext());
            }

            TextView textView = (TextView) convertView;
            textView.setPadding(40, 40, 40, 40);
            textView.setText(getItem(position));

            return convertView;
        }
    }

}
