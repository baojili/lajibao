package com.ln.base.network;


import com.google.gson.annotations.SerializedName;
import com.ln.base.model.JsonEntity;

import static com.ln.base.network.Config.PAGE_NUM;
import static com.ln.base.network.Config.PAGE_SIZE;

public class Page extends JsonEntity {
    @SerializedName(PAGE_SIZE)
    private Integer pageSize;
    @SerializedName(PAGE_NUM)
    private Integer pageNum;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer tempPageSize) {
        this.pageSize = tempPageSize;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer tempPageNum) {
        this.pageNum = tempPageNum;
    }
}
