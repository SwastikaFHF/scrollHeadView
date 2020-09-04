package com.aitangba.testproject.threadpool.volley;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by fhf11991 on 2017/5/27.
 */

public class BaseHttpActivity extends AppCompatActivity {

    Request.Tracker mTracker = new Request.Tracker();

    protected void requet(String url, Request.Listener listener) {
        mTracker.add(new HttpRequest(url, listener));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTracker.cancelAll();
    }
}
