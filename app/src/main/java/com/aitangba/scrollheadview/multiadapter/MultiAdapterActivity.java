package com.aitangba.scrollheadview.multiadapter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aitangba.scrollheadview.R;
import com.aitangba.scrollheadview.multiadapter.viewholder.BaseViewHolder;
import com.aitangba.scrollheadview.multiadapter.viewholder.CommonViewHolder;
import com.aitangba.scrollheadview.multiadapter.viewholder.EmptyViewHolder;
import com.aitangba.scrollheadview.multiadapter.viewholder.FooterViewHolder;
import com.aitangba.scrollheadview.multiadapter.viewholder.HeaderViewHolder;

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
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setData(getData(50));
        adapter.setHeadViewSize(2);
        adapter.setAutoLoadMore(true);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setEmptyViewStatus(EmptyViewHolder.STATUS_NO_DATA, true);
//                        adapter.setData(getData(0));
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
//                        adapter.addToFoot(getData(10));
                        adapter.setFooterViewStatus(FooterViewHolder.STATUS_NO_MORE, true);
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

    private static class Adapter extends CustomAdapter<String>{

        @Override
        public int getViewType(ItemType itemType, int position) {
            if(ItemType.HeaderView == itemType) {
                if(position == 0) {
                    return HeadText1ViewHolder.TYPE;
                } else {
                    return HeadText2ViewHolder.TYPE;
                }
            } else if(ItemType.CommonView == itemType) {
                int listDataPosition = position - getHeadViewSize();
                if(listDataPosition == 1) {
                    return TextViewHolder.TYPE;
                } else {
                    return ViewHolder.TYPE;
                }
            }
            return super.getViewType(itemType, position);
        }

        @Override
        protected BaseViewHolder onCreateViewHolder(ViewGroup parent, ItemType itemType, int viewType) {
            if(itemType == ItemType.HeaderView && viewType == HeadText1ViewHolder.TYPE) {
                return new HeadText1ViewHolder(new TextView(parent.getContext()));
            } else if(itemType == ItemType.HeaderView && viewType == HeadText2ViewHolder.TYPE) {
                return new HeadText2ViewHolder(new TextView(parent.getContext()));
            } else if(itemType == ItemType.EmptyView) {
                TextView textView = new TextView(parent.getContext());
                textView.setText("我是空数据页面");
                textView.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.colorPrimary));
                return new EmptyViewHolder(parent, textView);
            } else {
                return super.onCreateViewHolder(parent, itemType, viewType);
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

    private static class HeadText1ViewHolder extends HeaderViewHolder {
        public static final int TYPE = 1;
        private TextView mItemView;
        public HeadText1ViewHolder(View itemView) {
            super(itemView);
            mItemView = (TextView) itemView;
            mItemView.setText("HeadText1ViewHolder");
        }
    }

    private static class HeadText2ViewHolder extends HeaderViewHolder {
        public static final int TYPE = 2;
        private TextView mItemView;
        public HeadText2ViewHolder(View itemView) {
            super(itemView);
            mItemView = (TextView) itemView;
            mItemView.setText("HeadText2ViewHolder");
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
