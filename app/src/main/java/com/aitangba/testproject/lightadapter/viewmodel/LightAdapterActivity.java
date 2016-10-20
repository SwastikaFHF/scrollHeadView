package com.aitangba.testproject.lightadapter.viewmodel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.aitangba.testproject.R;
import com.aitangba.testproject.lightadapter.ItemFactory;
import com.aitangba.testproject.lightadapter.ui.Anim;
import com.aitangba.testproject.lightadapter.viewmodel.LightAdapter;

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
        mAdapter.setItemFactory(new AdapterFactory());
        mAdapter.setData(getData(20));
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

    private class AdapterFactory extends ItemFactory {
    }

    private static class AnimViewModel implements ViewModel<ViewHolder> {

        private Anim mAnim;

        public AnimViewModel(Anim anim) {
            mAnim = anim;
        }

        @Override
        public int getViewType() {
            return 0;
        }

        @Override
        public ViewHolder onCreateViewHolder() {
            return null;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder) {

        }

    }
}
