package com.aitangba.testproject.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.aitangba.testproject.view.drag.CustomScrollActivity;

public class FirstService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(ServiceTestActivity.TAG, "第一个service启动");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startService(new Intent(FirstService.this, SecondService.class));
            }
        }, 8000);
    }
}
