package com.aitangba.testproject.threadpool.volley;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2017/5/26.
 */

public class VolleyActivity extends BaseHttpActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley);

        final TextView textView = (TextView) findViewById(R.id.text);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Volley.with(v.getContext()).addRequest(new HttpRequest("https://www.baidu.com/", new Request.Listener() {

                    @Override
                    public void onResponse(String response) {
                        textView.setText(response);
                    }
                }));
            }
        });

        findViewById(R.id.shutdown_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Volley.with(v.getContext()).shutdown();
            }
        });
    }
}
