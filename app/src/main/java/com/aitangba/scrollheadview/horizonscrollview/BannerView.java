package com.aitangba.scrollheadview.horizonscrollview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aitangba.scrollheadview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2016/8/8.
 */
public class BannerView extends FrameLayout implements ViewPager.OnPageChangeListener {

    private static final int WHAT_AUTO_PLAY = 1000; //
    private final int mAutoPlayTime = 4000;  //ms 自动播放时间

    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private LinearLayout mDotLayout;
    private ImageView mImageDefault;

    private boolean mIsInTouchEvent;          //是否正在点击中，正在点击中需要暂停自动播放
    private boolean mAutoPlayAble = true;     //是否可以自动播放
    private boolean mIsAutoPlaying;           //是否正在自动播放
    private boolean mIsRecyclable = true;     //是否可以循环

    private int mCurrentPosition = 0;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.view_advertisement, this);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(mViewPagerAdapter = new ViewPagerAdapter());
        mViewPager.addOnPageChangeListener(this);

        mDotLayout = (LinearLayout) findViewById(R.id.ll_dots);
        mImageDefault = (ImageView) findViewById(R.id.iv_default);

        initAdvViews(null);
        initDotViews(null);
        startAutoPlay();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int actionMasked = ev.getAction() & MotionEvent.ACTION_MASK;
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mIsInTouchEvent = true;
                if(mAutoPlayAble) {
                    stopAutoPlay();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsInTouchEvent = false;
                if(mAutoPlayAble) {
                    startAutoPlay();
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void initAdvViews(List<String> adPictures) {
        adPictures = new ArrayList<>();
        adPictures.add("1");
        adPictures.add("2");
        adPictures.add("3");
        adPictures.add("4");

        // data size == 0
        if (adPictures == null || adPictures.size() == 0) {
            mImageDefault.setVisibility(VISIBLE);
            return;
        }
        mImageDefault.setVisibility(GONE);

        final int dataSize = adPictures.size();
        final List<View> adViews = new ArrayList<>();
        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        final boolean isRecyclable = mIsRecyclable && dataSize > 1;
        final int maxIndex = isRecyclable ? dataSize + 2 : dataSize;
        for (int i = 0; i < maxIndex; i++) {
            final int index;
            if(isRecyclable) {
                //cache the first imageView
                if (i == 0) {   //first index is the last view
                    index = dataSize - 1;
                } else if(i == maxIndex - 1) {  //last index is the first view
                    index = 0;
                } else {
                    final int FIRST_INDEX = 1; //every common view must sub a first index
                    index = i - FIRST_INDEX;
                }
            } else {
                index = i;
            }

            final String data = adPictures.get(index);
            ImageView adView = new ImageView(getContext());
            adView.setLayoutParams(params);
            adView.setScaleType(ImageView.ScaleType.FIT_XY);
            adView.setImageResource(R.mipmap.ic_launcher);
            adViews.add(adView);
        }
        mViewPagerAdapter.setData(adViews);
        mViewPager.setCurrentItem(isRecyclable ? 1 : 0);
    }

    private void initDotViews(List<String> configData) {
        configData = new ArrayList<>();
        configData.add("1");
        configData.add("2");
        configData.add("3");
        configData.add("4");

        mDotLayout.removeAllViews();
        int count = configData.size();

        // hide dot view if less than one
        if (count <= 1) {
            return;
        }

        final float scale = getResources().getDisplayMetrics().density;
        final int margin = (int)(10 * scale + 0.5F);
        for (int i = 0; i < count; i++) {
            final LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            if(i == 0) {
                params.setMargins(margin, 0, margin / 2, 0);
            } else if(i == count - 1) {
                params.setMargins(margin / 2, 0, margin, 0);
            } else {
                params.setMargins(margin / 2, 0, margin / 2, 0);
            }

            final ImageView dotView = new ImageView(getContext());
            dotView.setScaleType(ImageView.ScaleType.CENTER);
            dotView.setLayoutParams(params);

            if (i == 0) {
                dotView.setBackgroundResource(R.drawable.ic_ad_dot_focused);
            } else {
                dotView.setBackgroundResource(R.drawable.ic_ad_dot_unfocused);
            }
            mDotLayout.addView(dotView);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position;
        final int dataSize = getDataSize();
        if (dataSize <= 1) {
            return;
        }

        final boolean isRecyclable = mIsRecyclable;
        final int index;
        if(isRecyclable) {
            final int childViewSize = mViewPagerAdapter.getCount();
            //cache the first imageView
            if (position == 0) {   //first index is the last view
                index = dataSize - 1;
            } else if(position == childViewSize - 1) {  //last index is the first view
                index = 0;
            } else {
                final int FIRST_INDEX = 1; //every common view must sub a first index
                index = position - FIRST_INDEX;
            }
        } else {
            index = position;
        }

        for (int i = 0; i < dataSize; i++) {
            View childView = mDotLayout.getChildAt(i);
            if(childView instanceof ImageView) {
                ImageView image = (ImageView) mDotLayout.getChildAt(i);
                if (i == index) {
                    image.setBackgroundResource(R.drawable.ic_ad_dot_focused);
                } else {
                    image.setBackgroundResource(R.drawable.ic_ad_dot_unfocused);
                }
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        final int dataSize = getDataSize();
        if(state == ViewPager.SCROLL_STATE_IDLE && mIsRecyclable && dataSize > 1) {
            final int currentItem = mViewPager.getCurrentItem();
            final int childViewSize = mViewPagerAdapter.getCount();
            if(currentItem == 0) {
                mViewPager.setCurrentItem(childViewSize - 1 - 1, false);
            } else if(currentItem == childViewSize - 1) {
                mViewPager.setCurrentItem(1, false);
            }
        }
    }

    private int getDataSize() {
        return mDotLayout.getChildCount();
    }

    private Handler mAutoPlayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(mIsInTouchEvent) return;
            final int childViewSize = mViewPagerAdapter.getCount();
            mCurrentPosition  = (mCurrentPosition + 1) % childViewSize;
            mViewPager.setCurrentItem(mCurrentPosition, true);
            mAutoPlayHandler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, mAutoPlayTime);
        }
    };

    /**
     * 开始播放
     */
    public void startAutoPlay() {
        if (mAutoPlayAble && !mIsAutoPlaying) {
            mIsAutoPlaying = true;
            mAutoPlayHandler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, mAutoPlayTime);
        }
    }

    /**
     * 停止播放
     */
    public void stopAutoPlay() {
        if (mAutoPlayAble && mIsAutoPlaying) {
            mIsAutoPlaying = false;
            mAutoPlayHandler.removeMessages(WHAT_AUTO_PLAY);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        startAutoPlay();
        super.onDetachedFromWindow();
    }
}
