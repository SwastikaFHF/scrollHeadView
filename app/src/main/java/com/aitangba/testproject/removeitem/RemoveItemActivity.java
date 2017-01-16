package com.aitangba.testproject.removeitem;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aitangba.testproject.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2017/1/12.
 */

public class RemoveItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_item);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CustomAdapter adapter = new CustomAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setDate(getData(20));
    }

    private List<String> getData(int size) {
        List<String> list = new ArrayList<>();
        for(int i = 0;i < size ; i ++) {
            list.add("name" + i);
        }
        return list;
    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder> {

        List<String> mList = new ArrayList<>();

        private void setDate(List<String> list){
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_remove, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, final int position) {

            holder.mTextView.setText(mList.get(position));
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mList.size());
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    private static class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public CustomViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
