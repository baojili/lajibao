package com.ln.base.model;

import java.util.ArrayList;

/**
 *
 */

public class ContactSearchItem extends ContactItem {
    private int highlightedStart = 0;//需要高亮的开始下标
    private int highlightedEnd = 0;//需要高亮的结束下标
    private String matchPin = "";//用来匹配的拼音每个字的首字母比如：你好，NH
    private String namePinYin = "";//全名字拼音,比如：你好,NIHAO
    private ArrayList<String> namePinyinList = new ArrayList<>();//名字拼音集合，比如你好，NI,HAO
    private int matchIndex = 0;//匹配到号码后的下标

    public int getHighlightedStart() {
        return highlightedStart;
    }

    public void setHighlightedStart(int highlightedStart) {
        this.highlightedStart = highlightedStart;
    }

    public int getHighlightedEnd() {
        return highlightedEnd;
    }

    public void setHighlightedEnd(int highlightedEnd) {
        this.highlightedEnd = highlightedEnd;
    }

    public String getMatchPin() {
        return matchPin;
    }

    public void setMatchPin(String matchPin) {
        this.matchPin = matchPin;
    }

    public String getNamePinYin() {
        return namePinYin;
    }

    public void setNamePinYin(String namePinYin) {
        this.namePinYin = namePinYin;
    }

    public ArrayList<String> getNamePinyinList() {
        return namePinyinList;
    }

    public void setNamePinyinList(ArrayList<String> namePinyinList) {
        this.namePinyinList = namePinyinList;
    }

    public int getMatchIndex() {
        return matchIndex;
    }

    public void setMatchIndex(int matchIndex) {
        this.matchIndex = matchIndex;
    }
}
