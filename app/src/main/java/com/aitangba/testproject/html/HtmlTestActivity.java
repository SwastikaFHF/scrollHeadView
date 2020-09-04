package com.aitangba.testproject.html;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
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
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
        showDialog();
//        showAlertDialog();
    }

    private void showDialog() {
        Dialog dialog = new BaseDialog(this, R.style.Dialog_BottomPopup);
        dialog.setContentView(R.layout.user_agreement_dialog);
        TextView textView = dialog.findViewById(R.id.text);
//        textView.setMovementMethod(LinkMovementMethod.getInstance());
//        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        Spanned text = HtmlParser.getParser()
                .registerElement(new ImgElementHandler(getApplicationContext(), R.mipmap.ic_launcher, new ImgElementHandler.OnClickListener() {

                    @Override
                    public void onClick(@NonNull View widget, String tag) {

                    }
                }))
                .parse(getString(R.string.privacy_agreement));
        textView.setText(text);

        dialog.show();
    }

    private void showAlertDialog() {
        Dialog dialog = new AlertDialog(this) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.user_agreement_dialog);
                TextView textView = findViewById(R.id.text);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                Spanned text = HtmlParser.getParser()
                        .registerElement(new ImgElementHandler(getApplicationContext(), R.mipmap.ic_launcher, new ImgElementHandler.OnClickListener() {

                            @Override
                            public void onClick(@NonNull View widget, String tag) {

                            }
                        }))
                        .parse(getString(R.string.privacy_agreement));
                textView.setText(text);
            }
        };
        dialog.show();
    }
}
