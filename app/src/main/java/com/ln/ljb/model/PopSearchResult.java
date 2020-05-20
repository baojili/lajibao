package com.ln.ljb.model;

import com.ln.base.model.JsonEntity;

import java.util.List;

public class PopSearchResult extends JsonEntity {
    private List<String> words;

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }
}
