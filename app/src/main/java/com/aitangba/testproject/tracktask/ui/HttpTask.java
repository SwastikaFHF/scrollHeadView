package com.aitangba.testproject.tracktask.ui;

import android.support.v4.app.Fragment;
import android.view.View;

import com.aitangba.testproject.tracktask.TrackedAsyncTask;
import com.aitangba.testproject.tracktask.adapter.ActivityTaskAdapter;
import com.aitangba.testproject.tracktask.adapter.FragmentTaskAdapter;
import com.aitangba.testproject.tracktask.adapter.ViewTaskAdapter;

/**
 * Created by fhf11991 on 2018/3/26.
 */

public class HttpTask extends TrackedAsyncTask {

    public HttpTask(TrackedActivity activity) {
        super(new ActivityTaskAdapter(activity));
    }

    public HttpTask(Fragment fragment) {
        super(new FragmentTaskAdapter(fragment));
    }

    public HttpTask(View view) {
        super(new ViewTaskAdapter(view));
    }

    public void startRequest() {
        executeParallel(new Void[0]);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }
}
