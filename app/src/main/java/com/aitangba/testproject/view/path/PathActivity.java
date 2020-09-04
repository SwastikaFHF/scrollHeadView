package com.aitangba.testproject.view.path;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

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
