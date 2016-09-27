package com.aitangba.scrollheadview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.aitangba.scrollheadview.cornerrectangle.CornerRectangleActivity;
import com.aitangba.scrollheadview.customswipe.CustomSwipeActivity;
import com.aitangba.scrollheadview.edgeeffect.EffectActivity;
import com.aitangba.scrollheadview.horizonscroll.HorizonScrollActivity;
import com.aitangba.scrollheadview.horizonscroll.HorizonScrollTestActivity;
import com.aitangba.scrollheadview.multiadapter.ui.MultiAdapterActivity;
import com.aitangba.scrollheadview.path.PathActivity;
import com.aitangba.scrollheadview.progressbar.ProgressbarActivity;
import com.aitangba.scrollheadview.slideback.slidingmenu.SlidingMenuActivity;
import com.aitangba.scrollheadview.verticalscroll.VerticalScrollActivity;
import com.aitangba.scrollheadview.viewpager.ViewPageActivity;
import com.aitangba.scrollheadview.wheelview.WheelViewActivity;
import com.aitangba.scrollheadview.youtube.YoutubeActivity;

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
            case R.id.bt_view_page:
                startActivity(ViewPageActivity.class);
                break;
            case R.id.bt_corner_text:
                startActivity(CornerRectangleActivity.class);
                break;
            case R.id.bt_scroll_activity:
                startActivity(SlidingMenuActivity.class);
                break;
            case R.id.bt_horizon_scroll_activity:
                startActivity(com.aitangba.scrollheadview.horizonscrollview.HorizonScrollActivity.class);
                break;
            case R.id.bt_youtube_activity:
                startActivity(YoutubeActivity.class);
                break;
            case R.id.bt_effect_activity:
                startActivity(EffectActivity.class);
                break;
            case R.id.bt_progressbar_activity:
                startActivity(ProgressbarActivity.class);
                break;
            case R.id.bt_path_activity:
                startActivity(PathActivity.class);
                break;
            case R.id.bt_wheel_view:
                startActivity(WheelViewActivity.class);
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
