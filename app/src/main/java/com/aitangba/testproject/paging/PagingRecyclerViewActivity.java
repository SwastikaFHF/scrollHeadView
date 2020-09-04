package com.aitangba.testproject.paging;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.aitangba.testproject.R;
import com.aitangba.testproject.paging.view.OnLoadMoreListener;
import com.aitangba.testproject.paging.view.PagingRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by fhf11991 on 2017/5/11.
 */

public class PagingRecyclerViewActivity extends AppCompatActivity {

    private static final String TAG = "PagingActivity";
    private Adapter mAdapter;
    private PagingRecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paging_recycler_view);
        mRecyclerView = (PagingRecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.d(TAG, "onLoadMore ---");
                loadData();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter = new Adapter());

        View view = findViewById(R.id.emptyView);
        mRecyclerView.setEmptyView(view);

        findViewById(R.id.emptyTextBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.checkPaging(Collections.EMPTY_LIST);
                mAdapter.mList.clear();
                mAdapter.notifyDataSetChanged();
            }
        });

        loadData();
    }

    private List<String> getData(int size) {
        List<String> list = new ArrayList<>(size);
        for(int i= 0; i< size ; i++) {
            list.add( "名字" + (i + page * 10));
        }
        return list;
    }

    private int page = 0;
    private void loadData() {
        page = page + 1;

        if(page > 3) return;
        findViewById(Window.ID_ANDROID_CONTENT).postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "开始分页------ page = " + page);
                if(page == 1 || page == 2) {
                    List<String> list = getData(8);
                    mRecyclerView.checkPaging(list);
                    mAdapter.setData(list);
                } else if (page == 3) {
                    List<String> list = getData(2);
                    mRecyclerView.checkPaging(list);
                    mAdapter.setData(list);
                }

            }
        }, 2000);
    }

    static class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private List<String> mList = new ArrayList<>();

        public void setData(List<String> items) {
            mList.addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_light_adapter, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.mTextView.setText(mList.get(position));

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mList.remove(position);
                    notifyDataSetChanged();
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.name_text);
        }
    }
}
