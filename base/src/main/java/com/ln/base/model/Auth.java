package com.ln.base.model;

import android.content.Context;


public class Auth extends JsonEntity {

    private Long id;

    public Auth() {

    }

    public Auth(Context context) {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}