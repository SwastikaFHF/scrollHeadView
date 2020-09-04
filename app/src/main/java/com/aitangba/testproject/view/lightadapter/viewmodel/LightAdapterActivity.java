package com.aitangba.testproject.view.lightadapter.viewmodel;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aitangba.testproject.R;
import com.aitangba.testproject.databinding.ItemLightAdapterBinding;
import com.aitangba.testproject.view.lightadapter.ui.Anim;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2016/10/20.
 */

public class LightAdapterActivity extends AppCompatActivity {
    private LightAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_adapter);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter = new LightAdapter());
        mAdapter.setData(getData(20));
    }

    private List<AnimViewModel> getData(int size) {
        List<AnimViewModel> list = new ArrayList<>(size);
        for(int i= 0; i< size ; i++) {
            AnimViewModel anim = new AnimViewModel();
            anim.age = i;
            anim.name = "名字" + i;
            list.add(anim);
        }
        return list;
    }

    private static class AnimViewModel extends Anim implements ViewModel<LightAdapter, ItemLightAdapterBinding> {

        @Override
        public int getViewType() {
            return R.layout.item_light_adapter;
        }

        @Override
        public LightViewHolder<ItemLightAdapterBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
            View contentView = LayoutInflater.from(parent.getContext()).inflate(getViewType(), parent, false);
            return new LightViewHolder(contentView);
        }

        @Override
        public void onBindViewHolder(LightAdapter lightAdapter, ItemLightAdapterBinding dataBinding) {
            dataBinding.nameText.setText("你好");
        }
    }
}
