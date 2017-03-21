package com.aitangba.testproject.login;

import android.app.Application;

/**
 * Created by fhf11991 on 2017/1/11.
 */

public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        getMainLooper().setMessageLogging(new LooperMonitor());
    }

}
