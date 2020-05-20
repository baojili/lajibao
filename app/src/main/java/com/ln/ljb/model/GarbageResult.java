package com.ln.ljb.model;

import com.ln.base.model.JsonEntity;

import java.util.List;

public class GarbageResult extends JsonEntity {
    private List<JDGarbageVO> datas;//（Array[JDGarbageVO], optional): 检索返回数据信息 ,
    private String text;//（string, optional): 检索类型为1时 返回文字 2时返回图片地址

    public List<JDGarbageVO> getDatas() {
        return datas;
    }

    public void setDatas(List<JDGarbageVO> datas) {
        this.datas = datas;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
