package com.ln.base.tool;

import android.content.UriMatcher;
import android.net.Uri;
import android.webkit.URLUtil;

import androidx.annotation.Nullable;

import com.ln.base.R;
import com.ln.base.activity.BaseActivity;
import com.ln.base.activity.WebViewActivity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 处理OpenUrl匹配与跳转的工具类
 */

public class OpenUrlUtil {
    public static final String OPENURL_AUTHORITY = "app";
    public static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        for (UriDefine define : UriDefine.values()) {
            if (define == UriDefine.NONE) {
                continue;
            }
            addURICompatible(define.authority, define.path, define.ordinal());
        }
    }

    /**
     * Android 4.3以下不支持path以"/"开始的形式，所以此处兼容一下
     *
     * @param authority
     * @param path
     * @param id
     */
    private static void addURICompatible(String authority, @Nullable String path, int id) {
        if (path != null && path.charAt(0) == '/') {
            path = path.substring(1);
        }
        URI_MATCHER.addURI(authority, path, id);
    }

    public static void handleOpenUrl(BaseActivity activity, Uri uri) {
        if (uri == null || StringUtils.isEmpty(uri.toString())) {
            return;
        }
        int code = URI_MATCHER.match(uri);
        UriDefine define = code >= 0 && code < UriDefine.values().length ? UriDefine.values()[code] : UriDefine.NONE;
        switch (define) {
            default:
                if (URLUtil.isNetworkUrl(uri.toString())) {
                    String title = uri.getQueryParameter("title");
                    boolean needShare = getBooleanQueryParameter(uri, "needShare", false);
                    WebViewActivity.launch(activity, title, uri.toString(), needShare);
                }
                break;
        }
    }

    public static boolean getBooleanQueryParameter(Uri uri, String key, boolean defaultValue) {
        String flag = uri.getQueryParameter(key);
        if (flag == null) {
            return defaultValue;
        }
        flag = flag.toLowerCase(Locale.ROOT);
        return (!"false".equals(flag) && !"0".equals(flag));
    }

    public static UriDefine getUriPage(Uri uri) {
        int code = URI_MATCHER.match(uri);
        UriDefine define = code >= 0 && code < UriDefine.values().length ? UriDefine.values()[code] : UriDefine.NONE;
        return define;
    }

    public static boolean matchUri(Uri uri, UriDefine define) {
        if (uri != null && define != null) {
            int code = URI_MATCHER.match(uri);
            UriDefine matchDefine = code >= 0 && code < UriDefine.values().length ? UriDefine.values()[code] : UriDefine.NONE;
            return matchDefine.equals(define);
        }
        return false;
    }

    public enum WebTitleEnum {
        ORDER_DETAIL_TITLE(UriDefine.NONE, R.string.app_name);
        public static final Map WEB_TITLE_MAP = new HashMap<UriDefine, WebTitleEnum>();

        static {
            for (WebTitleEnum webTitle : WebTitleEnum.values()) {
                WEB_TITLE_MAP.put(webTitle.uriDefine, webTitle);
            }
        }

        public UriDefine uriDefine;
        public int titleRes;

        WebTitleEnum(UriDefine uriDefine, int titleRes) {
            this.uriDefine = uriDefine;
            this.titleRes = titleRes;
        }
    }


    public enum UriDefine {

        NONE(null);

        final String path;
        final String authority;

        UriDefine(String path) {
            this.path = path;
            authority = OPENURL_AUTHORITY;
        }

        UriDefine(String path, String authority) {
            this.path = path;
            this.authority = authority;
        }
    }
}
