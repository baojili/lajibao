package com.ln.base.model;

/**
 *
 */

public class ContactAddItem extends ContactItem {
    private transient String sortKey;

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

}
