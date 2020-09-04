package com.aitangba.testproject.invisiblefragment.task;

import androidx.fragment.app.Fragment;
import android.view.View;

import com.aitangba.testproject.invisiblefragment.task.adapter.ActivityTaskAdapter;
import com.aitangba.testproject.invisiblefragment.task.adapter.FragmentTaskAdapter;
import com.aitangba.testproject.invisiblefragment.task.adapter.ViewTaskAdapter;
import com.aitangba.testproject.tracktask.ui.TrackedActivity;

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
        executeParallel(new Object[]{});
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }
}
