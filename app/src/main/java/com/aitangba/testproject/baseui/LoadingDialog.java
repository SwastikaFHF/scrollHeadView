package com.aitangba.testproject.baseui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2017/3/21.
 */

public class LoadingDialog extends Dialog {

    private TextView mLoadingText;
    private ImageView mCloseImage;
    private ImageView mLoadingImage;
    private ObjectAnimator mObjectAnimator;

    public LoadingDialog(@NonNull Context context) {
        this(context, 0);
    }

    public LoadingDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.layout_loading_dialog);

        mLoadingText = (TextView) findViewById(R.id.loading_tip_text);
        mCloseImage = (ImageView) findViewById(R.id.close_image);
        mLoadingImage = (ImageView) findViewById(R.id.loading_image);

        mObjectAnimator = ObjectAnimator.ofFloat(mLoadingImage, View.ROTATION, 0f, 360f);
        mObjectAnimator.setInterpolator(new LinearInterpolator());
        mObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mObjectAnimator.setDuration(1000);
    }

    public void show(boolean cancelable, String message) {
        mCloseImage.setVisibility(cancelable ? View.VISIBLE : View.GONE);
        mLoadingText.setText(TextUtils.isEmpty(message) ? "加载中" : message);
        super.show();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mObjectAnimator.start();
    }

    @Override
    public void onDetachedFromWindow() {
        mObjectAnimator.cancel();
        super.onDetachedFromWindow();
    }
}
