package com.aitangba.testproject.login;

import android.os.SystemClock;
import android.util.Log;
import android.util.Printer;

/**
 * Created by fhf11991 on 2017/1/11.
 */

public class LooperMonitor implements Printer {
    private static final int DEFAULT_BLOCK_THRESHOLD_MILLIS = 3000;

    private long mBlockThresholdMillis = DEFAULT_BLOCK_THRESHOLD_MILLIS;

    private long mStartTimestamp = 0;
    private long mStartThreadTimestamp = 0;
    private boolean mPrintingStarted = false;

    @Override
    public void println(String x) {
        if (!mPrintingStarted) {
            mStartTimestamp = System.currentTimeMillis();
            mStartThreadTimestamp = SystemClock.currentThreadTimeMillis();
            mPrintingStarted = true;
        } else {
            final long endTime = System.currentTimeMillis();
            long mEndThreadTimestamp = SystemClock.currentThreadTimeMillis();
            Log.d("LooperMonitor", String.format("mStartTimestamp = %s , mStartThreadTimestamp = %s, endTime = %s, mEndThreadTimestamp = %s, cost time = %s ",
                    mStartTimestamp, mStartThreadTimestamp, endTime, mEndThreadTimestamp, endTime - mStartTimestamp));
            mPrintingStarted = false;
        }
    }

    private boolean isBlock(long endTime) {
        return endTime - mStartTimestamp > mBlockThresholdMillis;
    }
}
