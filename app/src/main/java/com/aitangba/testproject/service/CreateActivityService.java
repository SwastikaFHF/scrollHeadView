package com.aitangba.testproject.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.aitangba.testproject.view.drag.CustomScrollActivity;

public class CreateActivityService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CreateActivityService.this, "测试数据", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreateActivityService.this, CustomScrollActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                CreateActivityService.this.startActivity(intent);
            }
        }, 5000);
        return super.onStartCommand(intent, flags, startId);
    }
}
