package com.aitangba.testproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by XBeats on 2020/8/14
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static long sTimeStamp;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(sTimeStamp == 0) {
            sTimeStamp = System.currentTimeMillis();
        } else {
            long current = System.currentTimeMillis();
            Log.d("AlarmReceiver_TAG", "cost time = " + (current - sTimeStamp));
            sTimeStamp = current;
        }
        Toast.makeText(context, "测试信息", Toast.LENGTH_SHORT).show();
    }
}
