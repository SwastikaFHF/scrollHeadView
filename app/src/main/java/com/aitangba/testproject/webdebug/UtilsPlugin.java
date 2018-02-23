package com.aitangba.testproject.webdebug;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Created by zf08526 on 2015/7/31.
 */
public class UtilsPlugin extends BasePlugin {

    public UtilsPlugin(WebView webView) {
        super(webView);
    }

    @JavascriptInterface
    public void toast(String jsonStr){
        SyncEntity<ToastEntity.ReqParams> entity = getSyncParams(ToastEntity.ReqParams.class, jsonStr);
        if (entity == null) return;
        Toast.makeText(mWebView.getContext(), entity.getParams().content, Toast.LENGTH_SHORT).show();
    }
}
