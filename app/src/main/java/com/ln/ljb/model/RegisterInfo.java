package com.ln.ljb.model;

import com.ln.base.model.JsonEntity;

public class RegisterInfo extends JsonEntity {
    private String applyCode;//（string, optional): 推荐码 ,
    private String loginPwd;//（string, optional): 登录密码 ,
    private String mobile;//（string, optional): 手机号码 ,
    private String verfiyCode;//（string, optional): 验证码

    public String getApplyCode() {
        return applyCode;
    }

    public void setApplyCode(String applyCode) {
        this.applyCode = applyCode;
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
}
