package com.ln.base.model;

import com.ln.base.tool.JsonUtils;

public abstract class JsonEntity implements JsonInterface {

    @Override
    public String toString() {
        return JsonUtils.gson().toJson(this);
    }
}
