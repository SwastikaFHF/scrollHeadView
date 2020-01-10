package com.aitangba.testproject.html;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.aitangba.testproject.R;

/**
 * Created by XBeats on 2019/12/10
 */
public class HtmlTestActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        mTextView = (TextView) findViewById(R.id.text);
//        mTextView.setMovementMethod(LinkMovementMethod.getInstance());
//        mTextView.setText(HtmlParser.fromHtml(getString(R.string.privacy_agreement)));

        Dialog dialog = new BaseDialog(this, R.style.Dialog_Fullscreen) {
//            @Override
//            protected void onCreate(Bundle savedInstanceState) {
//                super.onCreate(savedInstanceState);
//                if (getWindow() != null) {
//                    WindowManager.LayoutParams p = getWindow().getAttributes();
//                    p.height = (int) (getDisplayMetrics(getApplicationContext()).heightPixels * 0.9f);
//                    p.gravity = Gravity.CENTER;
//                    getWindow().setAttributes(p);
//                }
//            }
        };
        dialog.setContentView(R.layout.user_agreement_dialog);
        TextView textView = dialog.findViewById(R.id.text);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        Spanned text = HtmlParser.getParser()
                .registerElement(new ImgElementHandler(getApplicationContext(), R.mipmap.ic_launcher, new ImgElementHandler.OnClickListener() {

                    @Override
                    public void onClick(@NonNull View widget, String tag) {

                    }
                }))
                .parse(getString(R.string.privacy_agreement));
        textView.setText(text);
        dialog.show();

        mTextView.post(new Runnable() {
            @Override
            public void run() {
                Rect rectangle= new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
                Log.d("Custom", "statusBar1 Height = " + rectangle.top);

                int viewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
                Log.d("Custom", "statusBar2 Height = " + rectangle.top);
            }
        });
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        if (context == null) {
            throw new IllegalArgumentException();
        }

        WindowManager windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics;
    }
}
