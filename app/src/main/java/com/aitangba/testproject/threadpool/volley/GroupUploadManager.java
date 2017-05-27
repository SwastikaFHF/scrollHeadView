package com.aitangba.testproject.threadpool.volley;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by fhf11991 on 2017/5/27.
 */

public class GroupUploadManager extends Handler {

    private static final int STATUS_PROGRESSING = 1;

    private List<UploadRequest> mRunnableList = new LinkedList<>(); //所有任务
    private List<String> mResultList = new ArrayList<>(); //返回数据列表

    private String mUrl;

    public GroupUploadManager(String url) {
        mUrl = url;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if(msg.what == STATUS_PROGRESSING) {
            mResultList.add(null);
        }
    }

    public GroupUploadManager addTasks(List<String> taskList) {
        final int size = taskList.size();
        mRunnableList.clear();

        UploadRequest walkRunnable;
        for (int i = 0; i < size; i++) {
            walkRunnable = new UploadRequest(mUrl, taskList.get(i), mListener);
            mRunnableList.add(walkRunnable);
        }
        return this;
    }

    public void start() {

    }

    Request.Listener mListener = new Request.Listener() {
        @Override
        public void onResponse(String response) {
            Message message = obtainMessage();
            message.what = STATUS_PROGRESSING;
            sendMessage(message);
        }
    };
}
