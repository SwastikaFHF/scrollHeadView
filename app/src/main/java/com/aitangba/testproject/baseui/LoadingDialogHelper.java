package com.aitangba.testproject.baseui;

import android.app.Activity;

/**
 * Created by fhf11991 on 2017/3/22.
 */

public class LoadingDialogHelper {

    private LoadingDialog mProgressDialog;

    private Activity mActivity;

    public LoadingDialogHelper(Activity activity) {
        mActivity = activity;
    }

    public void showLoadingDialog() {
        showLoadingDialog(false, null);
    }

    public void showLoadingDialog(boolean cancelable) {
        showLoadingDialog(cancelable, null);
    }

    public final void showLoadingDialog(final boolean cancelable, final String message) {
        mActivity.getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                getProgressDialog().show(cancelable, message);
            }
        });
    }

    private LoadingDialog getProgressDialog() {
        if(mProgressDialog == null) {
            mProgressDialog = new LoadingDialog(mActivity);
        }
        return mProgressDialog;
    }
}
