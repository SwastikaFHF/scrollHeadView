package com.aitangba.testproject.tracktask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by fhf11991 on 2018/3/26.
 */

public class HttpTask extends TrackedAsyncTask {

    public HttpTask(Activity activity) {

    }

    public HttpTask(Fragment fragment) {
    }

    public HttpTask(View view) {

        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {

            }
        });
    }

    public HttpTask(Dialog dialog) {
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }
}
