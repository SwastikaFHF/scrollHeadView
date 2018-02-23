package com.aitangba.testproject.webdebug;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.WebView;
import com.google.gson.Gson;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by zf08526 on 2015/7/29.
 */
public class BasePlugin {
    @NonNull
    private Handler mHandler = new Handler();
    protected WebView mWebView;

    BasePlugin(WebView webView) {
        mWebView = webView;
    }

    void runOnUIThread(@Nullable Runnable task) {
        if (task != null) {
            mHandler.post(task);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {}

    public void onRequestPermissionsResult() {}

    public void clear() {
        if (mWebView != null) {
            mWebView.removeJavascriptInterface(this.getClass().getSimpleName());
        }
    }

    @Nullable
    <T> SyncEntity<T> getSyncParams(Class<T> paramsClass, String jsonParams){
        try {
            Type objectType = buildType(SyncEntity.class, paramsClass);
            return new Gson().fromJson(jsonParams, objectType);
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    private ParameterizedType buildType(final Class<?> raw, final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return raw;
            }

            public Type[] getActualTypeArguments() {
                return args;
            }

            @Nullable
            public Type getOwnerType() {
                return null;
            }
        };
    }
}
