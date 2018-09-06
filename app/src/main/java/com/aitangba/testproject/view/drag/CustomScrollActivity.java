package com.aitangba.testproject.view.drag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.aitangba.testproject.R;
import com.aitangba.testproject.baseui.BaseActivity;

public class CustomScrollActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scroll);
        findViewById(R.id.text1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "测试信息", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
