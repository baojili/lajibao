package com.ln.base.network;


import com.google.gson.annotations.SerializedName;
import com.ln.base.model.JsonEntity;

import java.util.List;

import static com.ln.base.network.Config.PAGE_NUM;
import static com.ln.base.network.Config.PAGE_SIZE;
import static com.ln.base.network.Config.TOTAL_NUM;

public class PageResult<D> extends JsonEntity {
    @SerializedName(PAGE_NUM)
    private int pageNum;
    @SerializedName(PAGE_SIZE)
    private int pageSize;
    @SerializedName(TOTAL_NUM)
    private int totalNum;
    private List<D> dataList;

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
    }

    public List<D> getDataList() {
        return dataList;
    }

    public void setDataList(List<D> dataList) {
        this.dataList = dataList;
    }
}
