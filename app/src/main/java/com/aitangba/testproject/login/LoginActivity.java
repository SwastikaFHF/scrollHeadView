package com.aitangba.testproject.login;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2017/1/10.
 */

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_PARAM = "extraParam";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_test);

        final TextView textView = (TextView) findViewById(R.id.text);
        textView.setText(LoginActivity.class.getSimpleName());

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BaseLoginActivity.isLogin = true;
                String clsName = getIntent().getStringExtra(EXTRA_PARAM);
                ComponentName componentName = new ComponentName(LoginActivity.this, clsName);
                Intent intent = new Intent();
                intent.setComponent(componentName);
                startActivity(intent);
                finish();
            }
        });
    }
}
