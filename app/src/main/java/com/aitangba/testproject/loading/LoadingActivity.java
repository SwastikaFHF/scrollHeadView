package com.aitangba.testproject.loading;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2018/2/26.
 */

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        LoadingView loadingView = findViewById(R.id.loadingView);
        loadingView.start();
    }
}
