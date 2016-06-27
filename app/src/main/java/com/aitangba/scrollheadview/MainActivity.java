package com.aitangba.scrollheadview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.aitangba.scrollheadview.customswipe.CustomSwipeActivity;
import com.aitangba.scrollheadview.horizonscroll.HorizonScrollActivity;
import com.aitangba.scrollheadview.horizonscroll.HorizonScrollTestActivity;
import com.aitangba.scrollheadview.multiadapter.MultiAdapterActivity;
import com.aitangba.scrollheadview.verticalscroll.VerticalScrollActivity;

/**
 * Created by fhf11991 on 2016/6/22.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_horizon_scroll:
                startActivity(HorizonScrollActivity.class);
                break;
            case R.id.bt_vertical_scroll:
                startActivity(VerticalScrollActivity.class);
                break;
            case R.id.bt_custom_swipe:
                startActivity(CustomSwipeActivity.class);
                break;
            case R.id.bt_horizon_scroll_test:
                startActivity(HorizonScrollTestActivity.class);
                break;
            case R.id.bt_multi_adapter:
                startActivity(MultiAdapterActivity.class);
                break;
            default:break;
        }
    }

    private void startActivity(Class<?> activityClass) {
        if(activityClass != null) {
            startActivity(new Intent(this, activityClass));
        }
    }
}
