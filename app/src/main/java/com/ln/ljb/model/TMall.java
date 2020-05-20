package com.ln.ljb.model;

import com.ln.base.model.JsonEntity;

import java.util.List;

public class TMall extends JsonEntity {

    private String content;//（string, optional): 商品描述 ,
    private String createTime;//（string, optional): 创建时间 ,
    private Integer id;//（integer, optional): id ,
    private Integer itemGoldCount;//（integer, optional): 商品兑换所需金币 ,
    private Integer itemLevel;//（integer, optional): 商品兑换级别 ,
    private String pic;//（string, optional): 商品主图地址 ,
    private List<TMallPic> pics;//（Array[TMallPic], optional): 图片 ,
    private String remark;//（string, optional): 兑换说明 ,
    private Integer stock;//（integer, optional): 库存数量 ,
    private String title;//（string, optional): 商品名称

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getItemGoldCount() {
        return itemGoldCount;
    }

    public void setItemGoldCount(Integer itemGoldCount) {
        this.itemGoldCount = itemGoldCount;
    }

    public Integer getItemLevel() {
        return itemLevel;
    }

    public void setItemLevel(Integer itemLevel) {
        this.itemLevel = itemLevel;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public List<TMallPic> getPics() {
        return pics;
    }

    public void setPics(List<TMallPic> pics) {
        this.pics = pics;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
