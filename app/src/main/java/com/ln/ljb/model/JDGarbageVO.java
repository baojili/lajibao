package com.ln.ljb.model;

import com.ln.base.model.JsonEntity;

public class JDGarbageVO extends JsonEntity {
    private String cateName;//（string, optional): 垃圾分类 ,
    private String cityId;//（string, optional): 城市id ,
    private String cityName;//（string, optional): 城市名称 ,
    private String confidence;//（string, optional): 识别置信度 ,
    private String garbageName;//（string, optional): 垃圾名称 ,
    private String ps;//（string, optional): 备注描述

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getGarbageName() {
        return garbageName;
    }

    public void setGarbageName(String garbageName) {
        this.garbageName = garbageName;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }
}
