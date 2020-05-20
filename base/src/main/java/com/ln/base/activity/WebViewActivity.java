package com.ln.base.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.ln.base.R;
import com.ln.base.config.Config;
import com.ln.base.jsbridge.BridgeImpl;
import com.ln.base.jsbridge.JSBridge;
import com.ln.base.model.ContactInfo;
import com.ln.base.model.ShareItem;
import com.ln.base.tool.AndroidUtils;
import com.ln.base.tool.Log;
import com.ln.base.tool.OpenUrlUtil;
import com.ln.base.tool.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class WebViewActivity extends BaseActivity {

    private static final int CONTACT = 0;
    private static final String USER_ID_PLACEHOLDER = "{USERID}";
    protected WebView webView;
    protected String url = "";
    private boolean isDestroy = false;
    private String title = "";
    private String description = "";
    private String image = "";
    private String firstImage = "";
    private String defaultTitle = "";
    private String defaultContent = "";
    private boolean isNeedShare = false;
    private boolean canGoBack = true;
    private boolean isUseWebTitle = true;
    private boolean isAnimStart = false;
    private ProgressBar mBar;
    private boolean isCloseBack = false;
    private ValueCallback<Uri[]> mValueCallback;

    public static void launch(Activity context, String title, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("launch", true);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    public static void launch(Activity context, String title, String url, boolean needShare) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("launch", true);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        intent.putExtra("needShare", needShare);
        context.startActivity(intent);
    }

    public static void launch(Activity context, String title, String url, boolean needShare, boolean canGoBack) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("launch", true);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        intent.putExtra("needShare", needShare);
        intent.putExtra("canGoBack", canGoBack);
        context.startActivity(intent);
    }

    public static void launch(Activity context, String title, String url, boolean needShare, boolean canGoBack, boolean isUseWebTitle) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("launch", true);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        intent.putExtra("needShare", needShare);
        intent.putExtra("canGoBack", canGoBack);
        intent.putExtra("isUseWebTitle", isUseWebTitle);
        context.startActivity(intent);
    }

    protected void onCreate(Bundle savedInstanceState, int layoutResID) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResID);
        initView();

        if (!getIntent().hasExtra("launch"))
            return;
        if (!isNeedShare) {
            isNeedShare = getIntent().getBooleanExtra("needShare", false);
        }
        isUseWebTitle = getIntent().getBooleanExtra("isUseWebTitle", true);
        canGoBack = getIntent().getBooleanExtra("canGoBack", true);
        load(getIntent().getStringExtra("title"), getIntent().getStringExtra("url"));
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.onCreate(savedInstanceState, R.layout.activity_web_view);
    }

    protected void onCreate(Bundle savedInstanceState, int layoutResID, boolean needShare) {
        isNeedShare = needShare;
        this.onCreate(savedInstanceState, layoutResID);
    }

    /**
     * 重新修改默认的分享标题和内容
     *
     * @param title
     * @param content
     */
    protected void setDefaultShareTitleDesc(String title, String content) {
        this.defaultTitle = title;
        this.defaultContent = content;
    }

    protected void load(String title, String url) {
        if (title != null) {
            getTitleBar().setTitle(title);
        }
        if (url != null) {
            if (isAbsoluteUrl(url)) {
                Map<String, String> map = new HashMap<>();
                webView.loadUrl(url, map);
            } else {

                getLoadingView().error(getString(R.string.web_url_format_error));
            }
        } else {
            getLoadingView().error(getString(R.string.web_url_null));
        }
    }

    private boolean isAbsoluteUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        Uri uri = Uri.parse(url);
        return uri.isAbsolute();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initView() {
//        getLoadingView().loading(getString(R.string._loading_wait));
        mBar = findViewById(R.id.web_view_progress_bar);
        webView = findViewById(R.id.web_view);
        webView.addJavascriptInterface(new WebViewActivity.GetShareDataInterface(), "getShareData");
        webView.setWebViewClient(new WebViewActivity.ZWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int progress) {
                // 一开始加载就显示10%的进度，以提示用户正在加载
                final int minProgress = 10;
                final int maxProgress = 100;
                mBar.setProgress(progress > minProgress ? progress : minProgress);

                if (progress < maxProgress) {
                    mBar.setVisibility(View.VISIBLE);
                } else {
                    mBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isDestroy) {
                                mBar.setVisibility(View.GONE);
                            }
                        }
                    }, 200);
                }
                super.onProgressChanged(webView, progress);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                return true;
            }


            /**
             * For Android 4.1+
             * Tell the client to open a file chooser.
             * @param uploadFile A ValueCallback to set the URI of the file to upload.
             *      onReceiveValue must be called to wake up the thread.a
             * @param acceptType The value of the 'accept' attribute of the input tag
             *         associated with this file picker.
             * @param capture The value of the 'capture' attribute of the input tag
             *         associated with this file picker.
             */
            public void openFileChooser(ValueCallback<Uri> uploadFile, @NonNull String acceptType, String capture) {

            }

            /**
             * For Android 3+
             * Tell the client to open a file chooser.
             * @param uploadMsg A ValueCallback to set the URI of the file to upload.
             *      onReceiveValue must be called to wake up the thread.a
             * @param acceptType The value of the 'accept' attribute of the input tag
             *         associated with this file picker.
             *         associated with this file picker.
             */
            public void openFileChooser(ValueCallback<Uri> uploadMsg, @NonNull String acceptType) {

            }


            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                WebViewActivity.this.onReceivedWebTitle(view, title, view.getUrl());
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
                                      JsPromptResult result) {
                result.confirm(JSBridge.callJava(view, message));
                return true;
            }
        });

        JSBridge.register("ljb", BridgeImpl.class);
    }

    protected void onReceivedWebTitle(WebView view, String title, String url) {
        if (isDynamicTitle() && isUseWebTitle) {
            OpenUrlUtil.WebTitleEnum webTitleEnum = null;
            if (!TextUtils.isEmpty(url)) {
                OpenUrlUtil.UriDefine uriDefine = OpenUrlUtil.getUriPage(Uri.parse(url));
                webTitleEnum = (OpenUrlUtil.WebTitleEnum) OpenUrlUtil.WebTitleEnum.WEB_TITLE_MAP.get(uriDefine);
            }
            if (StringUtils.isValid(title) && !title.startsWith("http") && webTitleEnum == null) {
                getTitleBar().setTitle(title);
            }
        }
    }

    public ShareItem getShareItem() {
        ShareItem item = new ShareItem();
        if (StringUtils.isValid(title)) {
            item.setTitle(title);
        } else if (StringUtils.isValid(webView.getTitle())) {
            item.setTitle(webView.getTitle());
        } else if (StringUtils.isValid(defaultTitle)) {
            item.setTitle(defaultTitle);
        } else {
            item.setTitle(getString(R.string.app_name));
        }

        if (StringUtils.isValid(description)) {
            item.setDesContent(description);
        } else if (StringUtils.isValid(defaultContent)) {
            item.setDesContent(defaultContent);
        } else {
            item.setDesContent(getString(R.string.share_default_content));
        }

        if (StringUtils.isValid(image)) {
            item.setThumbUrl(image);
        } else if (StringUtils.isValid(firstImage)) {
            item.setThumbUrl(firstImage);
        } else {
            item.setThumbUrl(Config.APP_SHARE_LOGO_URL);
        }

        if (StringUtils.isValid(url)) {
            item.setTargetUrl(url);
        } else {
            item.setTargetUrl(webView.getUrl());
        }

        return item;
    }

    public void setCacheMode(int mode) {
        webView.getSettings().setCacheMode(mode);
    }

    @Override
    protected void onLoadingRetry() {
        super.onLoadingRetry();
//        getLoadingView().loading(getString(R.string._loading_wait));
        getLoadingView().h5Reloading();
        if (!TextUtils.isEmpty(webView.getUrl())) {
            webView.loadUrl(webView.getUrl());
        } else {
            webView.reload();
        }

    }

    @SuppressWarnings("ResourceType")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.drawable.bg_title_back) {
            processBack();
        } else if (view.getId() == R.drawable.ic_title_close) {
            finish();
        } else if (view.getId() == R.id.loading_retry) {
            onLoadingRetry();
        }
    }

    protected void processBack() {
        if (webView.canGoBack() && canGoBack) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isCloseBack) {
            processBack();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
    }

    protected boolean isDynamicTitle() {
        return true;
    }

    protected boolean isShowCloseIcon() {
        return false;
    }

    protected void overrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
    }

    protected void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        view.stopLoading();
    }

    protected void onPageFinished(WebView view, String url, int errorCode) {
        if (errorCode != 0)// load fail
        {
            getLoadingView().onError(errorCode, getString(R.string.page_load_fail));
            onReceivedWebTitle(view, getString(R.string.load_fail), url);
        } else {
            getLoadingView().onSuccess();
            onReceivedWebTitle(webView, view.getTitle(), url);
            //如果需要分享
            initShareBtn(url);
        }
        //如果需要关闭按钮
        initCloseBtn();


        /*view.loadUrl("javascript:hasCustomerMsg("+(settings.getBoolean(SharedPreferences.KEY_HAS_CUSTOMER_MSG,false)?1:0)+");");
        view.loadUrl("javascript:window.getShareData.OnGetShareTitle(document.getElementById(\"og-title\")?document.querySelector('meta[property=\\\"og:title\\\"]').getAttribute('content'):null);");
        view.loadUrl("javascript:window.getShareData.OnGetShareDescription(document.getElementById(\"og-description\")?document.querySelector('meta[property=\\\"og:description\\\"]').getAttribute('content'):null);");
        view.loadUrl("javascript:window.getShareData.OnGetShareImageUrl(document.getElementById(\"og-image\")?document.querySelector('meta[property=\\\"og:image\\\"]').getAttribute('content'):null);");
        view.loadUrl("javascript:window.getShareData.OnGetShareTargetUrl(document.getElementById(\"og-url\")?document.querySelector('meta[property=\\\"og:url\\\"]').getAttribute('content'):null);");
        view.loadUrl("javascript:window.getShareData.OnGetFirstImageUrl(document.getElementsByTagName(\"img\").length>0?document.getElementsByTagName(\"img\")[0].getAttribute(\"src\"):null);");*/

    }

    protected void onPageStarted(WebView view, String url, Bitmap favicon) {
//        getLoadingView().onStart(getString(R.string._loading_wait));
        initWebTitle(url);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }

    /**
     * 控制webview左边的关闭按钮是否显示
     */
    protected void initCloseBtn() {
        if (isShowCloseIcon()) {
            if (webView.canGoBack()) {
                getTitleBar().addIconButton(R.drawable.ic_title_close, true, this);
            } else {
                getTitleBar().removeButtonView(1, true);
            }
        }
    }

    /**
     * 控制右边的分享按钮的显示与隐藏
     */
    protected void initShareBtn(String url) {
        @SuppressWarnings("ResourceType") View shareView = getTitleBar().getRightBox().findViewById(R.drawable.bg_comm_detail_share);
        if (shareView != null) {
            getTitleBar().getRightBox().removeView(shareView);
        }
        if (isNeedShare) {
            getTitleBar().addIconButton(R.drawable.bg_comm_detail_share, false, new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    /*if (mShareUtil == null)
                    {
                        mShareUtil = new ShareUtil(WebViewActivity.this);
                    }
                    ShareItem shareItem = getShareItem();
                    mShareUtil.show(shareItem);*/
                }
            });
        }
    }

    private void initWebTitle(String url) {
        OpenUrlUtil.UriDefine uriDefine = OpenUrlUtil.getUriPage(Uri.parse(url));
        OpenUrlUtil.WebTitleEnum webTitleEnum = (OpenUrlUtil.WebTitleEnum) OpenUrlUtil.WebTitleEnum.WEB_TITLE_MAP.get(uriDefine);
        if (webTitleEnum != null) {
            //getTitleBar().setTitle(getString(webTitleEnum.titleRes));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT) {
            ContactInfo info = AndroidUtils.getContact(this, data);
//            Log.e(name + ":" + phoneNumber);
            if (info != null) {
                webView.loadUrl("javascript:setContacts(\"" + info.getPhone() + "\",\"" + info.getName() + "\");");
            }
//            et_name.setText(name);
//            et_phone.setText(phoneNumber.replace(" ",""));
        }
    }

    public void setNeedShare(boolean needShare) {
        isNeedShare = needShare;
    }

    public void closeBack() {
        getTitleBar().getLeftBox().removeAllViews();
        isCloseBack = true;
    }

    public void openBack() {
        getTitleBar().addTitleBackButton();
        isCloseBack = false;
    }

    private class ZWebViewClient extends WebViewClient {

        private int errorCode = 0;

        //解决重定向死循环的问题，这里要返回false，WebViewClient的默认实现已经返回false了；
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("shouldOverrideUrlLoading -> " + " url = " + url);
            return WebViewActivity.this.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            sslErrorHandler.proceed();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.d("onReceivedError -> " + " errorCode = " + errorCode
                    + "; description = " + description + "; failingUrl = "
                    + failingUrl);
            this.errorCode = errorCode;
            WebViewActivity.this.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("onPageFinished -> " + " url =" + url);
//            view.loadUrl("javascript:window.getShareData.OnGetHtmlBody(document.getElementsByTagName('body')[0].innerHTML);");
            WebViewActivity.this.onPageFinished(view, url, errorCode);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d("onPageStarted -> " + " url = " + url);
            errorCode = 0;
            WebViewActivity.this.onPageStarted(view, url, favicon);
//            initTitleBarStates(url);

        }


    }

    class GetShareDataInterface {
        @JavascriptInterface
        public void OnGetShareTitle(final String shareTitle) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    title = shareTitle;
                    Log.d("shareTitle is " + shareTitle + "");
                }
            });

        }

        @JavascriptInterface
        public void OnGetShareDescription(final String shareDescription) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    description = shareDescription;
                    Log.d("shareDescription is " + shareDescription + "");
                }
            });

        }

        @JavascriptInterface
        public void OnGetHtmlBody(final String body) {
           /*runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   if(body.contains("errCode")){
                       //TODO
                   }
               }
           });*/
        }

        @JavascriptInterface
        public void OnGetShareImageUrl(final String shareImageUrl) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    image = shareImageUrl;
                    Log.d("shareImageUrl is " + shareImageUrl + "");
                }
            });

        }

        @JavascriptInterface
        public void OnGetShareTargetUrl(final String shareUrl) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    url = shareUrl;
                    Log.d("shareUrl is " + shareUrl + "");
                }
            });

        }

        @JavascriptInterface
        public void OnGetFirstImageUrl(final String firstImageUrl) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (StringUtils.isValid(firstImageUrl)) {
                        //只取可用的图片地址
                        if (firstImageUrl.endsWith(".bmp") || firstImageUrl.endsWith(".jpg") || firstImageUrl.endsWith(".jpeg") || firstImageUrl.endsWith(".png")
                                || firstImageUrl.endsWith(".BMP") || firstImageUrl.endsWith(".JPG") || firstImageUrl.endsWith(".JPEG") || firstImageUrl.endsWith(".PNG")) {
                            if (firstImageUrl.startsWith("http")) {
                                firstImage = firstImageUrl;
                            } else {
                                firstImage = null;
                            }
                        }
                    } else {
                        firstImage = null;
                    }
                    Log.d(firstImage + "");
                }


            });


        }
    }
}