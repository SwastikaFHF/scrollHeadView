package com.aitangba.testproject.tracktask;

import android.support.v4.app.Fragment;
import android.view.View;

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
