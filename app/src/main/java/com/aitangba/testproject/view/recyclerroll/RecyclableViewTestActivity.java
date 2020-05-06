package com.aitangba.testproject.view.recyclerroll;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.aitangba.testproject.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XBeats on 2020/4/29
 * https://github.com/Marksss/InfiniteBanner
 */
public class RecyclableViewTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclable_view);

        // view pager
        ViewPager viewPage = findViewById(R.id.viewPage);
        List<View> views = new ArrayList<>();
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.bg_red);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                makeCall(v.getContext(), "18545679876");
                sendSms(v.getContext(), "18545679876", "测试信息");
//                Toast.makeText(v.getContext(), "第一张图片", Toast.LENGTH_SHORT).show();
            }
        });
        views.add(imageView);

        imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.bg_update_common);
        views.add(imageView);
        CustomPagerAdapter adapter = new CustomPagerAdapter(views);
        viewPage.setAdapter(adapter);

        // RecyclableViewGroup
        RecyclableViewGroup recyclableViewGroup = findViewById(R.id.recyclableViewGroup);
        imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.bg_red);
        recyclableViewGroup.addView(imageView);

        imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.bg_update_common);
        recyclableViewGroup.addView(imageView);
        recyclableViewGroup.start();

        // CustomViewGroup
        CustomViewGroup customViewGroup = findViewById(R.id.customView);
        customViewGroup.setOnPageChangeListener(new CustomViewGroup.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, int offset) {
                Log.d("RecyclableViewTest_TAG", "position " + position + ", offset = " + offset);
            }
        });
        customViewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "点击事件", Toast.LENGTH_SHORT).show();
            }
        });
        imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.bg_red);
        customViewGroup.addView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "第一张图片", Toast.LENGTH_SHORT).show();
            }
        });

        imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.bg_update_common);
        customViewGroup.addView(imageView);
    }


    private static class CustomPagerAdapter extends PagerAdapter {

        @NonNull
        private List<View> mViews = new ArrayList<>();

        private CustomPagerAdapter(@Nullable List<View> views) {
            if (views != null) {
                mViews.addAll(views);
            }
        }

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(mViews.get(position), 0);
            return mViews.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(mViews.get(position));
        }
    }

    public static void makeCall(Context context, String phoneNumber) {
        if (context == null || TextUtils.isEmpty(phoneNumber)) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        if (componentName == null) {
            Toast.makeText(context, "不能打电话", Toast.LENGTH_SHORT).show();
            return;
        }
        context.startActivity(intent);
    }

    public static void sendSms(Context context, String sentTo, String smsBody) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String packageName = Telephony.Sms.getDefaultSmsPackage(context);
            Intent smsIntent = new Intent(Intent.ACTION_SEND);
            smsIntent.setType("text/plain");
            smsIntent.putExtra(Intent.EXTRA_TEXT, smsBody);

            //if no default app is configured, then choose any app that support this intent.
            if (packageName != null) {
                smsIntent.setPackage(packageName);
                context.startActivity(smsIntent);
            }
        } else {
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", smsBody);
            smsIntent.putExtra("sms_body", "body");
            context.startActivity(smsIntent);
        }
    }
}
