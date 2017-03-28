package com.aitangba.testproject.paging.effect;

import com.aitangba.testproject.paging.view.StatefulViewHelper;

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
    public void onPreExecute() {

    }

    @Override
    public void onSuccess() {
        mStatefulViewHelper.dismiss();
    }

    @Override
    public void onError() {
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
