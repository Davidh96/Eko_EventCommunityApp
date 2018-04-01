package com.thedavehunt.eko;

public class ContactDoc {

    private String contactToken;
    private String contactID;
    private String contactName;

    public ContactDoc(String contactToken, String contactID, String contactName) {
        this.contactToken = contactToken;
        this.contactID = contactID;
        this.contactName = contactName;
    }

    public String getContactToken() {
        return contactToken;
    }

    public void setContactToken(String contactToken) {
        this.contactToken = contactToken;
    }

    public String getContactID() {
        return contactID;
    }

    public void setContactID(String contactID) {
        this.contactID = contactID;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}