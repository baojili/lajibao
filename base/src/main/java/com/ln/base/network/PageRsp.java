package com.ln.base.network;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;


public class PageRsp<D, T extends PageResult<D>> extends BaseRsp<T> implements PageRspInterface<D> {
    /*@SerializedName(PAGE_NUM)
    private int pageNum;
    @SerializedName(PAGE_SIZE)
    private int pageSize;
    @SerializedName(TOTAL_NUM)
    private int totalNum;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }*/

    @JsonIgnore
    @Override
    public List<D> getList() {
        return getData().getDataList();
    }
}
