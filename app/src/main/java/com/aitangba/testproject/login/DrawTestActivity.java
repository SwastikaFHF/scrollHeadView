package com.aitangba.testproject.login;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2017/1/10.
 */

public class DrawTestActivity extends AppCompatActivity implements Callback {

    private long onCreateStartTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        onCreateStartTime = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_test);
        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText(DrawTestActivity.class.getSimpleName());

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginRequestActivity.startActivity(DrawTestActivity.this);
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
