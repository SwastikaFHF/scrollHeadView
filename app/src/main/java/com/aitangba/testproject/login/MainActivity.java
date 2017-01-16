package com.aitangba.testproject.login;

import android.app.ActivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2017/1/10.
 */

public class MainActivity extends AppCompatActivity implements Callback {

    private long onCreateStartTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        onCreateStartTime = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_test);
        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText(MainActivity.class.getSimpleName());

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginRequestActivity.startActivity(MainActivity.this);
            }
        });

        CustomView customView = (CustomView) findViewById(R.id.custom);
        customView.setCallback(this);
    }

    @Override
    public boolean onDrawEnd(View view, String key) {
        long endDrawTime = System.currentTimeMillis();
        long costTime = endDrawTime - onCreateStartTime;
        Log.d("CustomView", String.format("onCreate = %s , onDrawEnd = %s, cost time = %s ms",
                onCreateStartTime, endDrawTime, costTime));
        return true;
    }
}
