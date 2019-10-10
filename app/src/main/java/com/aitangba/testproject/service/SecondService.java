package com.aitangba.testproject.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by XBeats on 2019/10/10
 */
public class SecondService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(ServiceTestActivity.TAG, "第二个service启动");
    }
}
