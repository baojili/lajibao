package com.ln.base.model;

/**
 *
 */

public class ContactItem extends JsonEntity {

    private String contactName;

    private String contactTel;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactTel() {
        return contactTel;
    }

    public void setContactTel(String contactTel) {
        this.contactTel = contactTel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactItem that = (ContactItem) o;

        if (contactName != null ? !contactName.equals(that.contactName) : that.contactName != null)
            return false;
        return contactTel != null ? contactTel.equals(that.contactTel) : that.contactTel == null;

    }

    @Override
    public int hashCode() {
        int result = contactName != null ? contactName.hashCode() : 0;
        result = 31 * result + (contactTel != null ? contactTel.hashCode() : 0);
        return result;
    }
}
