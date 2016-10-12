package com.aitangba.testproject.path;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2016/9/21.
 */

public class PathActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);

        final LoadingView loadingView = (LoadingView) findViewById(R.id.loading_view);

        findViewById(R.id.recycler_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingView.setStatus(LoadingView.Status.Loading);
            }
        });

        findViewById(R.id.success_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingView.setStatus(LoadingView.Status.Success);
            }
        });

        findViewById(R.id.failed_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingView.setStatus(LoadingView.Status.Failed);
            }
        });
    }

}
