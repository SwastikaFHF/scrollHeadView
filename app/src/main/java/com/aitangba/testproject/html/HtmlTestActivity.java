package com.aitangba.testproject.html;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;
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
        mTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mTextView.setText(HtmlUtils.fromHtml(getString(R.string.privacy_agreement)));

        Dialog dialog = new Dialog(this, R.style.Dialog_BottomPopup) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                if (getWindow() != null) {
                    WindowManager.LayoutParams p = getWindow().getAttributes();
                    p.height = (int) (getDisplayMetrics(getApplicationContext()).heightPixels * 0.9f);
                    p.gravity = Gravity.CENTER;
                    getWindow().setAttributes(p);
                }
            }
        };
        dialog.setContentView(R.layout.user_agreement_dialog);
        TextView textView = dialog.findViewById(R.id.text);
        ScrollView scrollView = dialog.findViewById(R.id.scrollView);
        textView.setText(HtmlUtils.fromHtml(getString(R.string.privacy_agreement_larger)));
        dialog.show();
        mTextView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("Custom", "post delayed: scrollview height = " + scrollView.getMeasuredHeight()
                        + ", width = " + scrollView.getMeasuredWidth()
                + ", dis = " + (scrollView.getBottom() - scrollView.getTop()));

                Log.d("Custom", "post delayed: textView height = " + textView.getMeasuredHeight()
                        + ", width = " + textView.getMeasuredWidth());

            }
        }, 2000);

        TextView textView1 = new TextView(this);
        textView1.setText(HtmlUtils.fromHtml(getString(R.string.privacy_agreement_larger)));
        int w = View.MeasureSpec.makeMeasureSpec(888,
                View.MeasureSpec.EXACTLY);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        //重新测量
        textView1.measure(w, h);
        Log.d("Custom", " tx h = " + textView1.getMeasuredHeight());
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
