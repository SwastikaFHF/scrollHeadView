package com.aitangba.testproject.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.aitangba.testproject.R;

/**
 * Created by Fring on 2020/9/7
 */
public class StandardDialogActivity extends FragmentActivity {

    public static final String TAG = "StandardDialog_TAG";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_standard_dialog);

        findViewById(R.id.test1Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = new ImageView(v.getContext());
                imageView.setBackgroundColor(Color.RED);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setImageResource(R.drawable.hw_dialog_text_img);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(StandardDialogActivity.this, "测试信息", Toast.LENGTH_SHORT).show();
                    }
                });
                StandardDialog standardDialog = new StandardDialog(v.getContext());
                standardDialog.setContentView(imageView);
                standardDialog.show();
            }
        });

        findViewById(R.id.test2Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = new ImageView(v.getContext());
                imageView.setBackgroundColor(Color.RED);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setImageResource(R.drawable.wh_dialog_text_img);
                StandardDialog standardDialog = new StandardDialog(v.getContext());
                standardDialog.setContentView(imageView);
                standardDialog.show();

            }
        });
    }
}
