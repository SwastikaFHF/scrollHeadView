package com.aitangba.testproject.slideback.slidinglayout;/**
 * Created by czy on 2015/7/3.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * user:czy
 * * Date: 2015-07-03
 * Time: 16:26
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        LastActivities.getInstance().removeView(getWindow().getDecorView());
        Log.e("test", "leave decor view =" + LastActivities.getInstance().getCount());
    }

    @Override
    protected void onDestroy() {
        LastActivities.getInstance().removeView(getWindow().getDecorView());
        super.onDestroy();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        LastActivities.getInstance().addView(getWindow().getDecorView());
    }

}
