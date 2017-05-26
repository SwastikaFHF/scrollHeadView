package com.aitangba.testproject.threadpool.volley;

import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by fhf11991 on 2017/5/25.
 */

public class Request implements Comparable<Request>, Runnable {

    public String name;

    @Override
    public int compareTo(@NonNull Request another) {
        return 0;
    }

    @Override
    public void run() {
        Log.d("Request", "run ---");
    }
}
