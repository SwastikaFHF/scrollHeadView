package com.aitangba.testproject.threadpool.volley;

import android.os.Handler;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by fhf11991 on 2017/5/27.
 */

public class ExecutorDelivery {

    private final Executor mResponsePoster;

    public ExecutorDelivery(final Handler handler) {
        mResponsePoster = new Executor() {
            @Override
            public void execute(@NonNull Runnable command) {
                handler.post(command);
            }
        };
    }

    public void postResponse(Request request, String response) {
        mResponsePoster.execute(new ResponseDeliveryRunnable(request, response));
    }

    private class ResponseDeliveryRunnable implements Runnable {
        private Request mRequest;
        private String mResponse;

        public ResponseDeliveryRunnable(Request request, String response) {
            mRequest = request;
            mResponse = response;
        }

        @Override
        public void run() {
            mRequest.deliverResponse(mResponse);
            mRequest.onFinish();
        }
    }
}
