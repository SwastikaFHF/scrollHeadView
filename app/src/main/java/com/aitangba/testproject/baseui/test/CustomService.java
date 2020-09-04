package com.aitangba.testproject.baseui.test;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.util.Log;

/**
 * Created by fhf11991 on 2017/3/21.
 */

public class CustomService extends IntentService {

    private final static String EXTRA_TYPE = "extraType";
    private final static int TYPE_START = 1;
    private final static int TYPE_STOP = 2;
    private int mType;
    private Handler mCustomHandler;

    public static void startService(Context context) {
        Intent intent = new Intent(context, CustomService.class);
        intent.putExtra(EXTRA_TYPE, TYPE_START);
        context.startService(intent);
    }

    public static void stopService(Context context) {
        Intent intent = new Intent(context, CustomService.class);
        intent.putExtra(EXTRA_TYPE, TYPE_STOP);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mCustomHandler = new Handler();
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);

        mType = intent.getIntExtra(EXTRA_TYPE, TYPE_START);
        if(mType == TYPE_STOP) {
            stopSelf();
        }
    }

    public CustomService() {
        super(CustomService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        while (mType == TYPE_START) {
            mCustomHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("CustomService", "onHandleIntent");
                }
            });
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
