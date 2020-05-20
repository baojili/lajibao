package com.ln.ljb.constant;

/**
 * 接口请求地址，所以的接口地址请求统一在此声明使用；
 */

public enum ApiPath {
    USER_REGISTER("user/register", "注册"),
    USER_RESET_LOGIN_PWD("user/resetloginpwd", "重置登录密码"),
    USER_INFO("user/get", "查询个人资料"),
    USER_LOGIN_MOBILE("user/login/mobile", "用户手机号码登录"),
    USER_LOGIN_WX("user/login/wx", "用户微信登录"),
    USER_PRIVACY("user/privacy", "隐私协议"),
    USER_SERVICE("user/service", "服务协议"),
    USER_UPDATE("user/update", "更新个人资料"),
    USER_UPLOAD("user/upload", "上传个人头像"),
    USER_BIND_MOBILE("user/bindMobile", "绑定手机号"),
    USER_BIND_We_CHAT("user/bindWx", "绑定微信"),
    MSG_LIST("msg/list", "消息列表"),
    MALL_GET("mall/get", "商品详情"),
    MALL_LIST("mall/list", "商品列表"),
    MALL_("mall/list", ""),
    GRABAGE_TEXT("grabage/text", "文字检索"),
    GRABAGE_IMAGE("grabage/image", "图片检索"),
    GRABAGE_VOICE("grabage/voice", "语音检索"),
    GRABAGE_POP_SEARCH("grabage/popsearch", "热门搜索"),
    SMS_SEND("sms/send", "发送短信验证码");

    private String path;
    private String desc;

    ApiPath(String path, String desc) {
        this.path = path;
        this.desc = desc;
    }

    public String path() {
        return path;
    }
}
