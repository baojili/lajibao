package com.ln.ljb.model;

import com.ln.base.model.JsonEntity;

public class LoginInfo extends JsonEntity {
    private String loginPwd;// (string, optional): 登录密码 ,
    private String mobile;// (string, optional): 手机号码

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
