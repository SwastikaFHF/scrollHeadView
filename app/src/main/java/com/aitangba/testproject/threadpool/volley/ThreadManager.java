package com.aitangba.testproject.threadpool.volley;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by fhf11991 on 2017/5/26.
 */

public class ThreadManager {

    private final static int CORE_NUM = 2;

    private int coreNum = CORE_NUM;
    private AtomicInteger threadNum = new AtomicInteger();
    private AtomicInteger workNum = new AtomicInteger();
    private AtomicInteger threadIndex = new AtomicInteger();

    private PriorityBlockingQueue<Request> mBlockingPriorityQueue = new PriorityBlockingQueue<>();

    public void execute(@NonNull Request command) {

        if(!addWorker(command)) {
            mBlockingPriorityQueue.offer(command);
            workNum.incrementAndGet();
        }
    }

    private boolean addWorker(Request request) {
        for(;;) {
            int threadNum = this.threadNum.get();
            int workerNum = this.workNum.get();
            Log.d("ThreadManager", "任务 named = " + request.name +  " threadNum = " + threadNum + " workerNum = " + workerNum);
            if(threadNum != 0 && workerNum < threadNum) { // some thread is sleep
                return false;
            } else {
                WorkerThread dispatcher = new WorkerThread("#" + threadIndex.incrementAndGet(), this, request);
                dispatcher.start();
                return true;
            }
        }
    }

    public static class WorkerThread extends Thread {

        private final static String TAG = "WorkerThread";

        private ThreadManager mThreadManager;
        private Request mFirstRequest;

        public WorkerThread(String threadName, ThreadManager threadManager, Request firstRequest) {
            super(threadName);
            this.mThreadManager = threadManager;
            mFirstRequest = firstRequest;

            mThreadManager.threadNum.incrementAndGet();
            mThreadManager.workNum.incrementAndGet();
            Log.d(TAG, "创建了一个新的线程 named = " + threadName);
        }

        @Override
        public void run() {
            super.run();

            Request request = mFirstRequest;
            mFirstRequest = null;
            boolean isCoreThread = false;

            try {
                retry:
                for(;;) {
                    while (request != null || (request = (isCoreThread ?
                            mThreadManager.mBlockingPriorityQueue.take()
                            : mThreadManager.mBlockingPriorityQueue.poll(1, TimeUnit.MILLISECONDS))) != null) {
                        request.run();
                        request = null;
                        mThreadManager.workNum.decrementAndGet();
                    }

                    if(mThreadManager.threadNum.get() <= mThreadManager.coreNum) {
                        isCoreThread = true;
                        continue retry;
                    } else {
                        break;
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mThreadManager.threadNum.decrementAndGet();
                Log.d(TAG, "线程" + Thread.currentThread().getName() + "关闭");
            }
        }
    }
}
