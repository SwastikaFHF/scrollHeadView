package com.aitangba.testproject.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2017/1/10.
 */

public class LoginRequestActivity extends BaseLoginActivity {

    public static void startActivity(Activity context) {
//        StartUiExtra startUiExtra = new StartUiExtra();
//        startUiExtra.claName = LoginRequestActivity.class.getName();
//        startActivityWithLogin(context, startUiExtra);

        Intent intent = new Intent(context, LoginRequestActivity.class);
        intent.putExtra("name", "zhangsan");

        Bundle bundle = new Bundle();
        bundle.putString("age", "ssssd");
        intent.putExtra("bl", bundle);

        context.startActivityForResult(intent, 101);
//        context.startActivity(intent);
    }

    @Override
    protected void onCreateWithLogin(@Nullable Bundle savedInstanceState) {
        super.onCreateWithLogin(savedInstanceState);
        setContentView(R.layout.activity_login_test);

        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText(LoginRequestActivity.class.getSimpleName());

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

}
