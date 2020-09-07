package com.aitangba.testproject.alarmmanager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.Toast;

/**
 * Created by Fring on 2020/9/7
 */
public class AlarmManager {


    public static void test(Activity activity) {
        Intent heartBeatIntent = new Intent();
        heartBeatIntent.setPackage(activity.getPackageName());
        heartBeatIntent.setAction("intent_alarm_log");
        activity.sendBroadcast(heartBeatIntent);

        if (isIgnoringBatteryOptimizations(activity)) {
            Toast.makeText(activity, "忽略电池优化", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "没有忽略电池优化", Toast.LENGTH_SHORT).show();
            isIgnoreBatteryOption(activity);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isIgnoringBatteryOptimizations(Context context) {
        String packageName = context.getPackageName();
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        if (pm.isIgnoringBatteryOptimizations(packageName)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 针对N以上的Doze模式
     *
     * @param activity
     */
    public static void isIgnoreBatteryOption(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Intent intent = new Intent();
                String packageName = activity.getPackageName();
                PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
// intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    activity.startActivityForResult(intent, 101);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
