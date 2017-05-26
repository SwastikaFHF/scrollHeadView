package com.aitangba.testproject.threadpool.volley;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.aitangba.testproject.R;

/**
 * Created by fhf11991 on 2017/5/26.
 */

public class VolleyActivity extends AppCompatActivity {

    ThreadManager mRequestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley);

        mRequestQueue = new ThreadManager();

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRunnable(4);
            }
        });

        findViewById(R.id.shutdown_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRequestQueue.shutdown();
            }
        });
    }

    private int mNum;

    private void getRunnable(int num) {
        for(int i = 0;i< num ;i ++) {
            Worker worker = new Worker(mNum);
            worker.name = "name " + mNum;
            mRequestQueue.execute(worker);
            mNum = mNum + 1;
        }
    }

    private static class Worker extends Request {

        private int num;

        public Worker(int num) {
            this.num = num;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(2000);
                Log.d("Worker", "编号 " + num + "执行结束");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
