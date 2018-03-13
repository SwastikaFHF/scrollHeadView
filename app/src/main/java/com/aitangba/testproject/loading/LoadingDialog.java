package com.aitangba.testproject.loading;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.aitangba.testproject.R;


public class LoadingDialog extends Dialog {

    private final LoadingView loadingView;

    public LoadingDialog(Context context){
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_loading);
        loadingView = findViewById(R.id.loadingView);
    }

    @Override
    public void show() {
        super.show();
        loadingView.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        loadingView.cancel();
    }
}
