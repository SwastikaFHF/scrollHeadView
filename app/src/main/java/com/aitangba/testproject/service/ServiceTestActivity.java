package com.aitangba.testproject.service;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.aitangba.testproject.R;

/**
 * Created by XBeats on 2019/10/9
 */
public class ServiceTestActivity extends AppCompatActivity {

    public static final String TAG = "ServiceTestTag";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_test);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ServiceTestActivity.this, "启动第一个service", Toast.LENGTH_SHORT).show();
                        startService(new Intent(ServiceTestActivity.this, FirstService.class));
                    }
                }, 10000);
            }
        });
    }
}
