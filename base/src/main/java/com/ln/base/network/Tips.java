package com.ln.base.network;

public enum Tips {
    LOGIN("登录中，请稍候…"), //
    LOGOUT("注销中，请稍候…"), //
    SENDING("发送中，请稍候…"), //
    SUBMIT("提交中，请稍候…"), //
    READIGN("读取中，请稍候…"),
    REQUEST("请求中，请稍候…"), //
    REGISTER("注册中，请稍候…"), //
    BINDING("绑定中，请稍候…"), //
    UPLOAD_IMAGE("准备上传图片，请稍候…"), //
    UPLOADING_IMAGE("上传中，请稍候…"), //
    UPLOAD_IMAGE_FAIL_RETRY("上传图片失败，请重试");

    private String msg;

    Tips(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}