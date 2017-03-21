package com.aitangba.testproject.baseui;

/**
 * Created by fhf11991 on 2017/3/21.
 */

public interface DialogManager {

    void showLoadingDialog();

    void showLoadingDialog(boolean cancelable);

    void showLoadingDialog(boolean cancelable, String message);

}
