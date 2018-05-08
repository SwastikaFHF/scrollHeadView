package com.aitangba.testproject.tracktask;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by fhf11991 on 2018/3/26.
 */

public class HttpTask extends TrackedAsyncTask {

    public HttpTask(TrackedActivity activity) {
        this(activity.getWindow().getDecorView());
    }

    public HttpTask(Fragment fragment) {
        this(fragment.getView());
    }

    public HttpTask(View view) {
        super(view);
    }

    public void startRequest() {
        executeParallel(new Void[0]);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }
}
