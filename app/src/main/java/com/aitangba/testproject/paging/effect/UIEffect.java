package com.aitangba.testproject.paging.effect;

import android.support.annotation.NonNull;

import com.aitangba.testproject.paging.Request;
import com.aitangba.testproject.paging.Response;

/**
 * Created by fhf11991 on 2017/3/28.
 */

public interface UIEffect {

    void onPreExecute(@NonNull Request request);

    void onSuccess(@NonNull Response response);

    void onError(@NonNull Response response);

    void onCancel();

}
