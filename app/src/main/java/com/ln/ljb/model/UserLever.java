package com.ln.ljb.model;

import com.ln.base.model.JsonEntity;

public class UserLever extends JsonEntity {
    private Integer appCode;// (integer, optional): 备用 app对应级别编码 ,
    private String iconUrl;// (string, optional): 级别图片 ,
    private Integer id;// (integer, optional): 级别id ,
    private Integer levelIntegral;// (integer, optional): 级别所需积分 ,
    private String levelName;// (string, optional): 级别描述信息

    public Integer getAppCode() {
        return appCode;
    }

    public void setAppCode(Integer appCode) {
        this.appCode = appCode;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLevelIntegral() {
        return levelIntegral;
    }

    public void setLevelIntegral(Integer levelIntegral) {
        this.levelIntegral = levelIntegral;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }
}
