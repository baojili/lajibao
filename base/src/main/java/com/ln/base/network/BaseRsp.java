package com.ln.base.network;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.SerializedName;

import static com.ln.base.network.Config.CODE;
import static com.ln.base.network.Config.DATA;
import static com.ln.base.network.Config.MSG;


/**
 * 通用返回类，接口返回格式要统一依照此标准
 */

public class BaseRsp<T> extends Rsp {

    @SerializedName(CODE)
    private Integer code;

    @SerializedName(MSG)
    private String msg;

    @SerializedName(DATA)
    private T data;

    public BaseRsp() {
    }

    public BaseRsp(Integer code, String msg) {
        setCode(code);
        setMsg(msg);
    }

    public BaseRsp(CodeMsg codeMsg) {
        setCode(codeMsg.code);
        setMsg(codeMsg.msg);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setCodeMsg(CodeMsg codeMsg) {
        setCode(codeMsg.code);
        setMsg(codeMsg.msg);
    }

    @JsonIgnore
    public boolean isSuccess() {
        return code == CodeMsg.OK.code;
    }
}

