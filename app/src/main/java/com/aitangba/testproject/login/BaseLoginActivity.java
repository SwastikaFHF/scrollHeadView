package com.aitangba.testproject.login;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by fhf11991 on 2017/1/10.
 */

public class BaseLoginActivity extends AppCompatActivity {

    public static final String EXTRA_PARAM = "extraParam";
    public static boolean isLogin = false;

    static void startActivityWithLogin(Context context, StartUiExtra startUiExtra) {
        final Intent intent = new Intent();
        if(isLogin) {
            ComponentName componentName = new ComponentName(context, startUiExtra.claName);
            intent.setComponent(componentName);
        } else {
            intent.setClass(context, LoginActivity.class);
            intent.putExtra(EXTRA_PARAM, startUiExtra.claName);
        }
        context.startActivity(intent);
    }

    @Override
    final protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(isLogin) {
            onCreateWithLogin(savedInstanceState);
        } else {
            Intent intent = getIntent();
            if(intent == null) {
                intent = new Intent();
            }
            intent.setClass(this, LoginActivity.class);
            intent.putExtra(EXTRA_PARAM, this.getClass().getName());
            startActivity(intent);
            finish();
        }
    }

    protected void onCreateWithLogin(@Nullable Bundle savedInstanceState) {

    }
}
