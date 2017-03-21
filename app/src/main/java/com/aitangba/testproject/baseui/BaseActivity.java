package com.aitangba.testproject.baseui;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by fhf11991 on 2017/3/21.
 */

public class BaseActivity extends AppCompatActivity implements DialogManager {

    private LoadingDialog mProgressDialog;

    @Override
    public void showLoadingDialog() {
        showLoadingDialog(false, null);
    }

    @Override
    public void showLoadingDialog(boolean cancelable) {
        showLoadingDialog(cancelable, null);
    }

    @Override
    public final void showLoadingDialog(final boolean cancelable, final String message) {
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                getProgressDialog().show(cancelable, message);
            }
        });
    }

    private LoadingDialog getProgressDialog() {
        if(mProgressDialog == null) {
            mProgressDialog = new LoadingDialog(this);
        }
        return mProgressDialog;
    }
}
