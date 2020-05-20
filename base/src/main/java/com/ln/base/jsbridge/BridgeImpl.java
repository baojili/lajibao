package com.ln.base.jsbridge;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;

import com.ln.base.activity.BaseActivity;
import com.ln.base.tool.AndroidUtils;
import com.ln.base.tool.Log;
import com.ln.base.tool.OpenUrlUtil;
import com.ln.base.tool.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class BridgeImpl implements IBridge {

    /**
     * h5中执行openurl协议
     */
    public static final int H5_OPEN_URL = 4;
    /**
     * 关闭返回
     */
    public static final int ACTION_BACK = 101;
    private static final int CONTACT = 0;

    public static void showToast(WebView webView, JSONObject param, final Callback callback) {
        String message = param.optString("msg");
        if (StringUtils.isValid(message)) {
            ((BaseActivity) (webView.getContext())).getToastDialog().showToast(message);
        }
    }

    public static void getAuth(WebView webView, JSONObject param, final Callback callback) {
        if (callback != null) {
            JSONObject object = getJSONObject(0, "ok");
            try {
                /*object.put("id", BaseActivity.auth.getId());
                object.put("operation", Integer.parseInt(BaseActivity.auth.getOperation()));
                object.put("version", BaseActivity.auth.getVersion());
                object.put("imei", BaseActivity.auth.getImei());*/
            } catch (Exception e) {
                e.printStackTrace();
            }
            callback.apply(object);
        }
    }

    public static void launchActivity(WebView webView, JSONObject param, final Callback callback) {
        int which = param.optInt("which");
        String jsonData = param.optString("jsonData");
        JSONObject paramData = null;
        if (jsonData != null) {
            try {
                // check its valid
                paramData = new JSONObject(jsonData);
            } catch (Exception e) {
                Log.d("Invalid JSON string:" + jsonData);
            }
        }
        switch (which) {
            case H5_OPEN_URL:
                String url = paramData.optString("openUrl");
                OpenUrlUtil.handleOpenUrl((BaseActivity) webView.getContext(), Uri.parse(url));
                break;
            default:
                break;
        }
    }

    public static void startVideo(WebView webView, JSONObject param, final Callback callback) {
        String videoAddress = param.optString("videoAddress");
        if (StringUtils.isValid(videoAddress)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(videoAddress), "video/*");
            webView.getContext().startActivity(intent);
        }
    }

    public static void getContacts(WebView webView, JSONObject param, final Callback callback) {
        ((Activity) webView.getContext()).startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.ContactsContract.Contacts.CONTENT_URI), CONTACT);
    }

    public static void callPhone(WebView webView, JSONObject param, final Callback callback) {
        String phoneNum = param.optString("phoneNum");
        if (StringUtils.isValid(phoneNum)) {
            Log.d("phoneNum" + phoneNum);
            AndroidUtils.openPhoneDial(webView.getContext(), phoneNum);
        }
    }

    private static JSONObject getJSONObject(int code, String msg) {
        JSONObject object = new JSONObject();
        try {
            object.put("code", code);
            object.put("msg", msg);
            return object;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
