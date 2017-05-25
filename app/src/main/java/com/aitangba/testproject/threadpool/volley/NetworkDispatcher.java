package com.aitangba.testproject.threadpool.volley;

import com.aitangba.testproject.threadpool.BlockingPriorityQueue;

import java.util.concurrent.TimeUnit;

/**
 * Created by fhf11991 on 2017/5/25.
 */

public class NetworkDispatcher extends Thread {

    private BlockingPriorityQueue<Request> mBlockingPriorityQueue;
    private RequestQueue requestQueue;

    private Request firstRequest;

    public NetworkDispatcher(RequestQueue requestQueue, BlockingPriorityQueue<Request> blockingPriorityQueue) {
        this.requestQueue = requestQueue;
        mBlockingPriorityQueue = blockingPriorityQueue;

        requestQueue.workNum.incrementAndGet();
    }

    public void setFirstRequest(Request firstRequest) {
        this.firstRequest = firstRequest;
    }

    @Override
    public void run() {
        super.run();

        Request request = firstRequest;
        firstRequest = null;
        try {
            while (request != null || (request = mBlockingPriorityQueue.poll(1, TimeUnit.MICROSECONDS)) != null) {
                request.run();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            requestQueue.workNum.decrementAndGet();
        }
    }
}
