package com.aitangba.testproject.paging.effect;

import com.aitangba.testproject.paging.Request;
import com.aitangba.testproject.paging.Response;
import com.aitangba.testproject.paging.helper.StatefulViewHelper;

/**
 * Created by fhf11991 on 2017/3/28.
 */

public class StatefulEffectImpl implements UIEffect {

    private StatefulViewHelper mStatefulViewHelper;
    private boolean mNeedStateful;

    private StatefulEffectImpl(StatefulViewHelper statefulViewHelper, boolean needStateful) {
        mStatefulViewHelper = statefulViewHelper;
        mNeedStateful = needStateful;
    }

    @Override
    public void onPreExecute(Request request) {

    }

    @Override
    public void onSuccess(Response response) {
//        mStatefulViewHelper.dismiss();
    }

    @Override
    public void onError(Response response) {
        mStatefulViewHelper.dismiss();
    }

    @Override
    public void onCancel() {
        mStatefulViewHelper.dismiss();
    }

    public static StatefulEffectImpl build(StatefulViewHelper statefulViewHelper,boolean needStateful) {
        if(statefulViewHelper == null) {
            throw new RuntimeException("statefulViewHelper can not be null !!!!");
        }
        return new StatefulEffectImpl(statefulViewHelper, needStateful);
    }
}
