package com.aitangba.testproject.paging.effect;

import com.aitangba.testproject.paging.helper.LoadingDialogHelper;

/**
 * Created by fhf11991 on 2017/3/28.
 */

public class DialogEffectImpl implements UIEffect {

    private LoadingDialogHelper mLoadingDialogHelper;
    private boolean mNeedDialog;

    private DialogEffectImpl(LoadingDialogHelper loadingDialogHelper, boolean needDialog) {
        mLoadingDialogHelper = loadingDialogHelper;
        mNeedDialog = needDialog;
    }

    @Override
    public void onPreExecute() {
        if(mNeedDialog) {
            mLoadingDialogHelper.showLoadingDialog();
        }
    }

    @Override
    public void onSuccess() {
        mLoadingDialogHelper.dismiss();
    }

    @Override
    public void onError() {
        mLoadingDialogHelper.dismiss();
    }

    @Override
    public void onCancel() {
        mLoadingDialogHelper.dismiss();
    }

    public static DialogEffectImpl build(LoadingDialogHelper loadingDialogHelper, boolean needDialog) {
        if(loadingDialogHelper == null) {
            throw new RuntimeException("loadingDialogHelper can not be null !!!!");
        }
        return new DialogEffectImpl(loadingDialogHelper, needDialog);
    }
}
