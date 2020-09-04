package com.aitangba.testproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.aitangba.testproject.MainActivity;
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

        if(savedInstanceState == null) {
            mMainTabAdapter.show(0);
        }

        findViewById(R.id.thirdButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FragmentTestActivity.this, MainActivity.class));
            }
        });
    }
}
