package com.aitangba.testproject.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2017/8/9.
 */

public class FragmentTestActivity extends AppCompatActivity {

    private MainTabAdapter mMainTabAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        mMainTabAdapter = new MainTabAdapter(getSupportFragmentManager(), findViewById(R.id.container_layout));
        findViewById(R.id.firstButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainTabAdapter.show(0);
            }
        });

        findViewById(R.id.secondButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainTabAdapter.show(1);
            }
        });
        mMainTabAdapter.show(0);
    }
}
