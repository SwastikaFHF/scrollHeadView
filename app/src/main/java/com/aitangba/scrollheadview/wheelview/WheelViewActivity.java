package com.aitangba.scrollheadview.wheelview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aitangba.scrollheadview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2016/9/27.
 */

public class WheelViewActivity extends AppCompatActivity {

    private ListAdapter mListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel_view);
        WheelView wheelView = (WheelView) findViewById(R.id.view_wheel);
        wheelView.setAdapter(mListAdapter = new ListAdapter());
        mListAdapter.setList(getDate(0, 10));

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListAdapter.setList(getDate(10, 10));
            }
        });
    }

    private List<String> getDate(int startIndex, int size) {
        List<String> list = new ArrayList<>();
        for(int i = 0 ; i < size ; i++) {
            list.add("数据" + (startIndex +  i));
        }
        return list;
    }

    private class ListAdapter extends BaseAdapter {

        List<String> mList = new ArrayList<>();

        public void setList(List<String> list) {
            mList.clear();
            if(list != null) {
                mList.addAll(list);
            }
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
                convertView = LayoutInflater.from(WheelViewActivity.this).inflate(R.layout.item_wheel, parent, false);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.text);
            textView.setText(getItem(position));
//            if(position % 4 == 0) {
//                convertView.setBackgroundColor(ContextCompat.getColor(WheelViewActivity.this, R.color.colorAccent));
//            } else {
//                convertView.setBackgroundColor(ContextCompat.getColor(WheelViewActivity.this, android.R.color.white));
//            }

            return convertView;
        }
    }
}
