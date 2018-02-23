package com.aitangba.testproject.webdebug;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.aitangba.testproject.BuildConfig;
import com.aitangba.testproject.R;
import com.aitangba.testproject.databinding.ActivityWebDebugBinding;

/**
 * Created by fhf11991 on 2018/2/23.
 */

public class WebDebugActivity extends AppCompatActivity {

    private ActivityWebDebugBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_debug);

        initWebView();
        binding.webView.loadUrl("file:///android_asset/javascript.html");
    }

    private void initWebView() {
        addPlugins();
        WebSettings settings = binding.webView.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowFileAccessFromFileURLs(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
        }

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setPluginState(WebSettings.PluginState.ON);

        binding.webView.setVerticalScrollBarEnabled(false);
        binding.webView.setHorizontalScrollBarEnabled(true);
        binding.webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        binding.webView.removeJavascriptInterface("searchBoxJavaBridge_");
        binding.webView.removeJavascriptInterface("accessibility");
        binding.webView.removeJavascriptInterface("accessibilityTraversal");
        binding.webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        settings.setUserAgentString(settings.getUserAgentString() + "/chebada/" + "1.0.0");
        if (Build.VERSION.SDK_INT >= 11) {
            settings.setAllowContentAccess(true);
        }
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

        CookieManager.getInstance().setAcceptCookie(true);

        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        settings.setGeolocationEnabled(true);

        WebViewClient webClient = new WebViewClient() {
            @Override
            public void onPageFinished(@NonNull WebView view, String url) {
                super.onPageFinished(view, url);
                addPlugins();// 中兴手机必须这么干一次
            }
        };
        binding.webView.setWebViewClient(webClient);
    }

    private void addPlugins() {
        UtilsPlugin utilsPlugin = new UtilsPlugin(binding.webView);
        binding.webView.addJavascriptInterface(utilsPlugin, UtilsPlugin.class.getSimpleName());
    }
}
