package com.aitangba.testproject.threadpool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by fhf11991 on 2017/4/6.
 */
public class UploadManager extends Handler {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT * 2;
    private static final int MSG_SUCCESS = 101;
    private static final int MSG_FAILED = 102;

    private static final int STATUS_INIT = 0;
    private static final int STATUS_PROGRESSING = 1;
    private static final int STATUS_SUCCESS = 2;
    private static final int STATUS_FAILED = 3;
    private static final int STATUS_CANCELED = 4;

    private int status = STATUS_INIT;

    public ThreadPoolExecutor mExecutorService;
    private List<String> mTaskList = new ArrayList<>();
    private List<String> mResultList = new ArrayList<>();

    private List<WalkRunnable> runnableList = new ArrayList<>();

    public UploadManager addTasks(List<String> taskList) {
        mTaskList.clear();
        mTaskList.addAll(taskList);
        return this;
    }

    public void start() {
        final int size = mTaskList.size();
        if (size == 0) return;

        if (mExecutorService == null) {
            mExecutorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(CORE_POOL_SIZE);
        }

        status = STATUS_PROGRESSING;
        runnableList.clear();
        for (int i = 0; i < size; i++) {
            mExecutorService.submit(new WalkRunnable(this, mTaskList.get(i), size));
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if(status == STATUS_FAILED || status == STATUS_CANCELED) return;

        if(msg.what == MSG_SUCCESS) {
            status = STATUS_SUCCESS;
            if(mCallback != null) {
                mCallback.onSuccess(mResultList);
            }
        } else if(msg.what == MSG_FAILED) {
            status = STATUS_FAILED;
            if(mCallback != null) {
                mCallback.onFail(msg.getData().getString("task", ""));
            }
        }
    }

    public void stop() {
        status = STATUS_CANCELED;
        if (mExecutorService != null) {
            mExecutorService.shutdownNow();
        }

        if(!mExecutorService.isTerminated()) {
            Iterator<WalkRunnable> threadIterator = runnableList.iterator();
            while(threadIterator.hasNext()){
                WalkRunnable walkRunnable = threadIterator.next();
                walkRunnable.stop();
            }
        }
        runnableList.clear();
    }

    private static class WalkRunnable implements Runnable {

        private static final String TAG = "uploadFile";
        private static final int TIME_OUT = 10 * 1000; // 超时时间
        private static final String CHARSET = "utf-8"; // 设置编码
        private static final String urlStr = "http://10.1.158.61:9090/upload";

        private UploadManager mUploadManager;
        private final String task;
        private final int taskSize;
        private HttpURLConnection mConn;

        public WalkRunnable(UploadManager uploadManager, String task, int taskSize) {
            this.task = task;
            this.taskSize = taskSize;
            this.mUploadManager = uploadManager;
        }

        @Override
        public void run() {
            synchronized (mUploadManager.runnableList) {
                if(!mUploadManager.runnableList.contains(this)) {
                    mUploadManager.runnableList.add(this);
                }
            }

            File file = new File(task);

            String result = null;
            String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
            String PREFIX = "--", LINE_END = "\r\n";
            String CONTENT_TYPE = "multipart/form-data"; // 内容类型
            try {
                URL url = new URL(urlStr);
                mConn = (HttpURLConnection) url.openConnection();
                mConn.setReadTimeout(TIME_OUT);
                mConn.setConnectTimeout(TIME_OUT);
                mConn.setDoInput(true); // 允许输入流
                mConn.setDoOutput(true); // 允许输出流
                mConn.setUseCaches(false); // 不允许使用缓存
                mConn.setRequestMethod("POST"); // 请求方式
                mConn.setRequestProperty("Charset", CHARSET); // 设置编码
                mConn.setRequestProperty("connection", "keep-alive");
                mConn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
                if (file != null) {
                    DataOutputStream dos = new DataOutputStream(mConn.getOutputStream());
                    StringBuffer sb = new StringBuffer();
                    sb.append(PREFIX);
                    sb.append(BOUNDARY);
                    sb.append(LINE_END);
                    sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"" + LINE_END);
                    sb.append("Content-Type:image/jpeg" + LINE_END); // 这里配置的Content-type很重要的 ，用于服务器端辨别文件的类型的
                    sb.append(LINE_END);
                    dos.write(sb.toString().getBytes());
                    InputStream is = new FileInputStream(file);
                    byte[] bytes = new byte[1024];
                    int len;
                    while ((len = is.read(bytes)) != -1) {
                        dos.write(bytes, 0, len);
                    }
                    is.close();
                    dos.write(LINE_END.getBytes());
                    byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                    dos.write(end_data);
                    dos.flush();
                    int res = mConn.getResponseCode();
                    Log.e(TAG, "response code:" + res);
                    Log.e(TAG, "request success");
                    InputStream input = mConn.getInputStream();
                    StringBuffer sb1 = new StringBuffer();
                    int ss;
                    while ((ss = input.read()) != -1) {
                        sb1.append((char) ss);
                    }
                    result = sb1.toString();
                    Log.e(TAG, "result : " + result);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Message message = mUploadManager.obtainMessage();
                message.what = MSG_FAILED;
                Bundle bundle = new Bundle();
                bundle.putString("task", task);
                message.setData(bundle);
                mUploadManager.sendMessage(message);
                return;
            } catch (IOException e) {
                e.printStackTrace();
                Message message = mUploadManager.obtainMessage();
                message.what = MSG_FAILED;
                Bundle bundle = new Bundle();
                bundle.putString("task", task);
                message.setData(bundle);
                mUploadManager.sendMessage(message);
                return;
            }

            mUploadManager.runnableList.remove(this);
            synchronized (mUploadManager.mResultList) {
                mUploadManager.mResultList.add(result);
                if (mUploadManager.mResultList.size() == taskSize) {
                    mUploadManager.stop();
                    mUploadManager.sendEmptyMessage(MSG_SUCCESS);
                }
            }
        }

        public void stop() {
            if(mConn != null) {
                mConn.disconnect();
            }
        }
    }

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onSuccess(List<String> resultList);
        void onFail(String task);
    }
}
