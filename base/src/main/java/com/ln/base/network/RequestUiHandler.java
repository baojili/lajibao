package com.ln.base.network;

/**
 * 负责网络请求的UI状态显示类
 */

public interface RequestUiHandler {

    void onStart(String hint);

    void onError(int errcode, String errMsg);

    void onSuccess();

}
