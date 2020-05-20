package com.ln.base.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;


public class WebView extends android.webkit.WebView {

    public WebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WebView(Context context) {
        super(context);
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            onScrollChanged(getScrollX(), getScrollY(), getScrollX(),
                    getScrollY());
        }
        return super.onTouchEvent(ev);
    }

    private void init() {
        WebSettings webSetting = this.getSettings();
        webSetting.setUserAgentString(getSettings().getUserAgentString() + " chnsunjy");
        webSetting.setDomStorageEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setAppCachePath(getContext().getApplicationContext().getCacheDir().getAbsolutePath());
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setUseWideViewPort(true);
//		IX5WebViewExtension ix5 = getX5WebViewExtension();
//		if (null != ix5) {
//			ix5.setScrollBarFadingEnabled(false);
//		}

        try {
            webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        } catch (NoClassDefFoundError e) {  //真烦恼，有些机型上木有。。。
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getSettings().setLoadsImagesAutomatically(true);
        } else {
            getSettings().setLoadsImagesAutomatically(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        }
        setBackgroundColor(0);
        setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

    }

}