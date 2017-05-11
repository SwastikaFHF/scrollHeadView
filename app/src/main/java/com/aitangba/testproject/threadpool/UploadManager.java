package com.aitangba.testproject.threadpool;

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
import java.util.Vector;
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
    private static final int STATUS_FINISHED = 2;

    private int status = STATUS_INIT;

    public ThreadPoolExecutor mExecutorService;
    private List<WalkRunnable> mRunnableList = new ArrayList<>(); //所有任务
    private List<String> mResultList = new ArrayList<>(); //返回数据列表

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public UploadManager addTasks(List<String> taskList) {
        final int size = taskList.size();
        mRunnableList.clear();
        WalkRunnable walkRunnable;
        for (int i = 0; i < size; i++) {
            walkRunnable = new WalkRunnable(this, taskList.get(i), size);
            mRunnableList.add(walkRunnable);
        }
        return this;
    }

    public void start() {
        final int size = mRunnableList.size();
        if (size == 0) return;

        if (mExecutorService == null) {
            mExecutorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(CORE_POOL_SIZE);
        }

        status = STATUS_PROGRESSING;

        for (int i = 0; i < size; i++) {
            mExecutorService.submit(mRunnableList.get(i));
        }
    }

    private void stop() {
        if (mExecutorService != null) {
            mExecutorService.shutdownNow();
        }

        // stop all runnable
        if(!mExecutorService.isTerminated()) {
            Iterator<WalkRunnable> threadIterator = mRunnableList.iterator();
            while(threadIterator.hasNext()){
                WalkRunnable walkRunnable = threadIterator.next();
                walkRunnable.stop();
            }
        }
        mRunnableList.clear();
    }

    public void cancel() {
        sendEmptyMessage(MSG_FAILED);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if(status == STATUS_FINISHED) return;

        if(msg.what == MSG_SUCCESS) {
            status = STATUS_FINISHED;
            if(mCallback != null) {
                mCallback.onSuccess(mResultList);
            }
        } else if(msg.what == MSG_FAILED) {
            status = STATUS_FINISHED;
            stop();
            if(mCallback != null) {
                mCallback.onFail();
            }
        }
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
                mUploadManager.sendEmptyMessage(MSG_FAILED);
                return;
            } catch (IOException e) {
                e.printStackTrace();
                mUploadManager.sendEmptyMessage(MSG_FAILED);
                return;
            } finally {
                mConn = null;
            }

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

    public interface Callback {
        void onSuccess(List<String> resultList);
        void onFail();
    }
}
