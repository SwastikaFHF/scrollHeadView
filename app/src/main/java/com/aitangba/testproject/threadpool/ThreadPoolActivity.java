package com.aitangba.testproject.threadpool;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aitangba.testproject.R;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;

/**
 * Created by fhf11991 on 2017/4/7.
 */

public class ThreadPoolActivity extends AppCompatActivity {

    private TextView mTextView;
    private ArrayList<String> mPhotos;
    UploadManager uploadManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_pool);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setShowCamera(true)
                        .setShowGif(true)
                        .setPreviewEnabled(true)
                        .start(ThreadPoolActivity.this, PhotoPicker.REQUEST_CODE);
            }
        });

        findViewById(R.id.upload_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadManager = new UploadManager();
                uploadManager.setCallback(new UploadManager.Callback() {
                    @Override
                    public void onSuccess(List<String> resultList) {
                        Log.d("ThreadPoolActivity", "上传成功");
                        mTextView.setText("上传成功");
                        Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(String task) {
                        Log.d("ThreadPoolActivity", "取消上传");
                        mTextView.setText("取消上传");
                        Toast.makeText(getApplicationContext(), "取消上传", Toast.LENGTH_SHORT).show();
                    }
                });
                uploadManager.addTasks(mPhotos).start();
            }
        });

        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadManager.stop();
            }
        });

        mTextView = (TextView) findViewById(R.id.text);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                mPhotos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                mTextView.setText(JSON.toJSONString(mPhotos));
            }
        }
    }
}
