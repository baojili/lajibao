package com.ln.ljb.model;

import com.ln.base.model.JsonEntity;

public class ResetPasswordInfo extends JsonEntity {
    private Long id;//用户id
    private String reLoginPwd;//（string, optional): 二次确认密码,
    private String loginPwd;//（string, optional): 登录密码 ,
    private String mobile;//（string, optional): 手机号码 ,
    private String verfiyCode;//（string, optional): 验证码

    public String getReLoginPwd() {
        return reLoginPwd;
    }

    public void setReLoginPwd(String reLoginPwd) {
        this.reLoginPwd = reLoginPwd;
    }

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

    public String getVerfiyCode() {
        return verfiyCode;
    }

    public void setVerfiyCode(String verfiyCode) {
        this.verfiyCode = verfiyCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
