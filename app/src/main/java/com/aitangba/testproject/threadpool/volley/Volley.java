package com.aitangba.testproject.threadpool.volley;

import android.content.Context;

/**
 * Created by fhf11991 on 2017/5/27.
 */

public class Volley {

    static volatile Volley singleton = null;

    private Volley() {
        mThreadManager = new ThreadManager();
    }

    public static Volley with(Context context) {
        if (singleton == null) {
            synchronized (Volley.class) {
                if (singleton == null) {
                    singleton = new Builder(context).build();
                }
            }
        }
        return singleton;
    }

    private ThreadManager mThreadManager;

    public void addRequest(Request request) {
        mThreadManager.execute(request);
    }

    public void shutdown() {
        mThreadManager.shutdown();
    }

    public static class Builder {
        private Context mContext;

        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            mContext = context.getApplicationContext();
        }

        private Volley build() {
            return new Volley();
        }
    }
}
