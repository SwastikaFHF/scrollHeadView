package com.aitangba.testproject.threadpool.volley;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashSet;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by fhf11991 on 2017/5/26.
 */

public class ThreadManager {

    private final static int CORE_NUM = 2;

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

    private int coreNum = CORE_NUM;
    private AtomicInteger workersCount = new AtomicInteger();
    private AtomicInteger currentJobsCount = new AtomicInteger(); // jobs num from running to waiting
    private volatile boolean isShutdown = false;

    private final HashSet<Worker> workers = new HashSet<>();
    private final ReentrantLock mainLock = new ReentrantLock();

    private PriorityBlockingQueue<Request> mBlockingPriorityQueue = new PriorityBlockingQueue<>();

    public void execute(@NonNull Request command) {
        if(isShutdown) {
            return;
        }

        if(!addWorker(command)) {
            mBlockingPriorityQueue.offer(command);
            currentJobsCount.incrementAndGet();
        }
    }

    public void shutdown() {
        isShutdown = true;
        mBlockingPriorityQueue.clear();

        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (Worker w : workers) {
                Log.d("ThreadManager", "开始关闭线程");
                w.close();
            }
            workers.clear();
        } finally {
            mainLock.unlock();
        }
    }

    private int threadIndex;

    protected Thread newThread(Runnable r) {
        String threadName = "#" + threadIndex ++;
        Log.d("ThreadManager", "创建了一个新的线程 named = " + threadName);
        return new Thread(r, threadName);
    }

    private boolean addWorker(Request request) {
        for(;;) {
            int threadNum = this.workersCount.get();
            int workerNum = this.currentJobsCount.get();
            Log.d("ThreadManager", "任务 named = " + request.name +  " workersCount = " + threadNum + " workerNum = " + workerNum);
            if(workerNum == MAXIMUM_POOL_SIZE) {
                return false;
            } else if(threadNum != 0 && workerNum < threadNum) { // some thread is sleep
                return false;
            } else {
                Worker dispatcher = new Worker(this, request);
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    workers.add(dispatcher);
                } finally {
                    mainLock.unlock();
                }
                dispatcher.thread.start();
                return true;
            }
        }
    }

    public static class Worker implements Runnable {

        private final static String TAG = "Worker";

        private ThreadManager mThreadManager;
        private Request mCurrentRequest;
        private Thread thread;

        public Worker(ThreadManager threadManager, Request firstRequest) {
            this.mThreadManager = threadManager;
            mCurrentRequest = firstRequest;
            thread = threadManager.newThread(this);

            mThreadManager.workersCount.incrementAndGet();
            mThreadManager.currentJobsCount.incrementAndGet();
        }

        @Override
        public void run() {
            boolean isCoreThread = false;
            try {
                retry:
                for(;;) {
                    while (!mThreadManager.isShutdown
                            && (mCurrentRequest != null || (mCurrentRequest = (isCoreThread ? mThreadManager.mBlockingPriorityQueue.take()
                            : mThreadManager.mBlockingPriorityQueue.poll(1, TimeUnit.MILLISECONDS))) != null)) {
                        mCurrentRequest.run();
                        mCurrentRequest = null;
                        isCoreThread = false;
                        mThreadManager.currentJobsCount.decrementAndGet();
                    }

                    if(mThreadManager.isShutdown) {
                        break;
                    } else if(mThreadManager.workersCount.get() <= mThreadManager.coreNum) {
                        isCoreThread = true;
                        continue retry;
                    } else {
                        break;
                    }
                }

            } catch (InterruptedException e) {

            } finally {
                final ReentrantLock mainLock = mThreadManager.mainLock;
                mainLock.lock();
                try {
                    mThreadManager.workers.remove(this);
                } finally {
                    mainLock.unlock();
                }
                mThreadManager.workersCount.decrementAndGet();
                Log.d(TAG, "线程" + Thread.currentThread().getName() + "关闭");
            }
        }

        private void close() {
            if(mCurrentRequest != null) {
                mCurrentRequest.close();
                mCurrentRequest = null;
            }

            if (!thread.isInterrupted()) {
                try {
                    thread.interrupt();
                } catch (SecurityException ignore) {
                }
            }
        }
    }
}
