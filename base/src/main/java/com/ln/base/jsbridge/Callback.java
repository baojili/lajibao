package com.ln.base.jsbridge;

import android.os.Handler;
import android.os.Looper;
import android.webkit.WebView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;


public class Callback {
    private static final String CALLBACK_JS_FORMAT = "javascript:JSBridge.onFinish('%s', %s);";
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private String callBack;
    private WeakReference<WebView> mWebViewRef;

    public Callback(WebView view, String callBack) {
        mWebViewRef = new WeakReference<>(view);
        this.callBack = callBack;
    }

    public void apply(JSONObject jsonObject) {
        final String execJs = String.format(CALLBACK_JS_FORMAT, callBack, String.valueOf(jsonObject));
        if (mWebViewRef != null && mWebViewRef.get() != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mWebViewRef.get().loadUrl(execJs);
                }
            });

        }

    }
}
