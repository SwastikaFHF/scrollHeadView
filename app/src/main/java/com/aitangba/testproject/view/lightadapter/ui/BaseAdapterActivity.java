package com.aitangba.testproject.view.lightadapter.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aitangba.testproject.R;
import com.aitangba.testproject.view.lightadapter.BaseAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2016/10/13.
 */

public class BaseAdapterActivity extends AppCompatActivity {

    private BaseAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_light_adapter);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter = new BaseAdapter());

        String json = new Gson().toJson(getData(20));

        Log.d("BaseAdapterActivity", "json ==  " + json);

        List<Mouse> mouses = new Gson().fromJson(json, new TypeToken<ArrayList<Mouse>>(){}.getType());
        Log.d("BaseAdapterActivity", "mouses ==  " + new Gson().toJson(mouses));

        CatViewModel cat = new CatViewModel();
        cat.legs = "猫腿";

//        items.add(cat);

        mAdapter.setData(mouses);

    }

    private List<Anim> getData(int size) {
        List<Anim> list = new ArrayList<>(size);
        for(int i= 0; i< size ; i++) {
            Anim anim = new Anim();
            anim.age = i;
            anim.name = "名字" + i;
            list.add(anim);
        }
        return list;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class Mouse extends Anim implements BaseAdapter.Item<ViewHolder> {


        @Override
        public int getItemViewId() {
            return R.layout.item_light_adapter;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View contentView = LayoutInflater.from(parent.getContext()).inflate(getItemViewId(), parent, false);
            return new ViewHolder(contentView);
        }

        public void onBindViewHolder(BaseAdapter baseAdapter, ViewHolder catViewHolder) {

        }
    }

    private static class CatViewHolder extends RecyclerView.ViewHolder {

        public CatViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class CatViewModel extends Cat implements BaseAdapter.Item<CatViewHolder> {

        @Override
        public int getItemViewId() {
            return R.layout.item_light_adapter_cat;
        }

        @Override
        public CatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View contentView = LayoutInflater.from(parent.getContext()).inflate(getItemViewId(), parent, false);
            return new CatViewHolder(contentView);
        }

        public void onBindViewHolder(BaseAdapter baseAdapter, CatViewHolder catViewHolder) {

        }
    }

    public interface Model{

    }

    private class AnimViewModel implements Model {

        public AnimViewModel(Anim anim) {

        }

        public int getViewType() {
            return  0;
        }
    }
}
