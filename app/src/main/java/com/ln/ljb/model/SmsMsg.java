package com.ln.ljb.model;

import com.ln.base.model.JsonEntity;

public class SmsMsg extends JsonEntity {
    private String mobile;//（string, optional): 手机号码 ,
    private Integer smsType;//（integer, optional): 验证码类型 0 短信验证码 1 语音验证码 ,
    private Integer type;//（integer, optional): 验证码类型 1登录验证码 2 重置密码  3 绑定手机号码

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getSmsType() {
        return smsType;
    }

    public void setSmsType(Integer smsType) {
        this.smsType = smsType;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
