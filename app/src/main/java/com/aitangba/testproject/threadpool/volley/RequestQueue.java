package com.aitangba.testproject.threadpool.volley;

import com.aitangba.testproject.threadpool.BlockingPriorityQueue;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by fhf11991 on 2017/5/25.
 */

public class RequestQueue {

    protected AtomicInteger workNum = new AtomicInteger();
    private BlockingPriorityQueue<Request> mBlockingPriorityQueue = new BlockingPriorityQueue<>();

    public void addRequest(Request request) {

        mBlockingPriorityQueue.offer(request);

        if(workNum.get() < 4) {
            NetworkDispatcher dispatcher = new NetworkDispatcher(this, mBlockingPriorityQueue);
            dispatcher.setFirstRequest(request);
            dispatcher.start();
        } else {

        }
    }

}
