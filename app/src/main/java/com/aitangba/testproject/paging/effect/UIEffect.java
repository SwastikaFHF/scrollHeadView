package com.aitangba.testproject.paging.effect;

import com.aitangba.testproject.paging.HttpTask;

/**
 * Created by fhf11991 on 2017/3/28.
 */

public interface UIEffect {

    void onPreExecute(HttpTask httpTask);

    void onSuccess();

    void onError();

    void onCancel();

}
