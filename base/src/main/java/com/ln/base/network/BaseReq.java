package com.ln.base.network;

import com.google.gson.annotations.SerializedName;

import static com.ln.base.network.Config.BASE;

public class BaseReq<T> extends Req {

    @SerializedName(BASE)
    private Base base;
    private T data;

    public BaseReq() {
        base = new Base();
    }

    public BaseReq(T data) {
        this();
        this.data = data;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}