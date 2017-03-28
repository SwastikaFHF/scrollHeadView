package com.aitangba.testproject.paging.effect;

/**
 * Created by fhf11991 on 2017/3/28.
 */

public interface UIEffect {

    void onPreExecute();

    void onSuccess();

    void onError();

    void onCancel();

}
