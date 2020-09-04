package com.aitangba.testproject.loading;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
        loadingView.cancel();

        new LoadingDialog(this).show();
    }
}
