package com.aitangba.testproject.view.nestwebview;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.aitangba.testproject.R;

import java.lang.reflect.Method;

/**
 * Created by XBeats on 2020/7/30
 */
public class NestWebViewActivity extends FragmentActivity {

    private WebView mWebView;
    private View textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nest_webview);

        textView = findViewById(R.id.text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.setScrollY(200);
            }
        });

        mWebView = findViewById(R.id.webView);
        initWebView();

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mWebView.getLayoutParams();
        layoutParams.height = getDisplayMetrics(this).heightPixels - getStatusBarHeight(this);
        mWebView.requestLayout();

        mWebView.loadUrl("https://zhuanti.chebada.com/zhuanti/release/app/2019/02/linestemplate/?refid=1033315721&specialRecommendId=5a832c293cd4435d2460e518f45a3ea9&v=1203#/");
    }

    public static int getStatusBarHeight(Context context) {
        final Resources resources = context.getResources();
        final int heightResId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (heightResId > 0) {
            return resources.getDimensionPixelSize(heightResId);
        } else {
            int heightFromDp = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 24 : 25;
            return (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, heightFromDp, resources.getDisplayMetrics());
        }
    }

    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpValue, context.getApplicationContext().getResources().getDisplayMetrics());
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        if (context == null) {
            throw new IllegalArgumentException();
        }

        WindowManager windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics;
    }

    public void initWebView(){
        WebSettings settings = mWebView.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowFileAccessFromFileURLs(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
        }

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setPluginState(WebSettings.PluginState.ON);

        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        mWebView.removeJavascriptInterface("accessibility");
        mWebView.removeJavascriptInterface("accessibilityTraversal");
        mWebView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        mWebView.setWebViewClient(new WebViewClient(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        settings.setAllowContentAccess(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(false);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setAllowFileAccess(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setAppCacheEnabled(true);
        settings.setSavePassword(false);
        settings.setGeolocationEnabled(true);
        String dir = getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setGeolocationDatabasePath(dir);

        // 设置H5自动播放音乐
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        } else {
            Class<?> clazz = settings.getClass();
            try {
                Method method = clazz.getDeclaredMethod("setMediaPlaybackRequiresUserGesture", boolean.class);
                method.invoke(settings, false);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        CookieManager.getInstance().setAcceptCookie(true);
    }
}
