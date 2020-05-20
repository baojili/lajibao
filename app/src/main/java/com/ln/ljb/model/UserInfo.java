package com.ln.ljb.model;

import com.ln.base.model.JsonEntity;

public class UserInfo extends JsonEntity {
    private Integer applyCode;// (integer, optional): 推荐码 ,
    private String city;//(string, optional): 城市 ,
    private Integer gold;//(integer, optional): 金币数 ,
    private Long id;//(integer, optional): id ,
    private Integer currentDayIntegral; //(integer, optional): 今日获得环保积分 ,
    private Integer integral;// (integer, optional): 积分数 ,
    private Integer totalIntegral;// (integer, optional): 总积分数
    private String lastLoginTime;//(string, optional): 上次登录时间 ,
    private Integer level;//(integer, optional): 用户等级 ,
    private String mobile;//(string, optional): 手机号码 ,
    private String name;//（string, optional): 姓名 ,
    private Integer picId;//（integer, optional): 头像id ,
    private String picUrl;//（string, optional): 头像地址 ,
    private String province;//（string, optional): 省份 ,
    private String registerTime;//（string, optional): 注册时间 ,
    private Integer sex;//（integer, optional): 性别 1 男 2 女 ,
    private String wxNick;//（string, optional): 微信昵称;
    private String wxId;//微信ID
    private String deviceToken;// 友盟token ,
    private UserLever tLevel;// 级别详细信息 ,

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public Integer getApplyCode() {
        return applyCode;
    }

    public void setApplyCode(Integer applyCode) {
        this.applyCode = applyCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getGold() {
        return gold;
    }

    public void setGold(Integer gold) {
        this.gold = gold;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIntegral() {
        return integral;
    }

    public void setIntegral(Integer integral) {
        this.integral = integral;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPicId() {
        return picId;
    }

    public void setPicId(Integer picId) {
        this.picId = picId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getWxNick() {
        return wxNick;
    }

    public void setWxNick(String wxNick) {
        this.wxNick = wxNick;
    }

    public String getWxId() {
        return wxId;
    }

    public void setWxId(String wxId) {
        this.wxId = wxId;
    }

    public Integer getCurrentDayIntegral() {
        return currentDayIntegral;
    }

    public void setCurrentDayIntegral(Integer currentDayIntegral) {
        this.currentDayIntegral = currentDayIntegral;
    }

    public Integer getTotalIntegral() {
        return totalIntegral;
    }

    public void setTotalIntegral(Integer totalIntegral) {
        this.totalIntegral = totalIntegral;
    }

    public UserLever gettLevel() {
        return tLevel;
    }

    public void settLevel(UserLever tLevel) {
        this.tLevel = tLevel;
    }
}
