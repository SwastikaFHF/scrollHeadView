package com.aitangba.testproject.paging.effect;

import com.aitangba.testproject.paging.Request;
import com.aitangba.testproject.paging.Response;

/**
 * Created by fhf11991 on 2017/3/28.
 */

public interface UIEffect {

    void onPreExecute(Request request);

    void onSuccess(Response response);

    void onError(Response response);

    void onCancel();

}
