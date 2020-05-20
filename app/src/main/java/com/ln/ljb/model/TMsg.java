package com.ln.ljb.model;

import com.ln.base.model.JsonEntity;

public class TMsg extends JsonEntity {
    private String content;//（string, optional),
    private String createTime;//（string, optional),
    private Long id;//（integer, optional),
    private String state;//（string, optional),
    private String title;//（string, optional)

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
