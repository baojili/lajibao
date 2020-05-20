package com.ln.ljb.model;

import com.ln.base.model.JsonEntity;

public class TMallPic extends JsonEntity {
    private String createTime;//（string, optional),
    private Integer id;//（integer, optional),
    private Integer mallId;//（integer, optional),
    private String url;//（string, optional)

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMallId() {
        return mallId;
    }

    public void setMallId(Integer mallId) {
        this.mallId = mallId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
