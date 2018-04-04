package com.thedavehunt.eko;

public class ContactDoc {

    private String contactToken;
    private String contactID;
    private String contactName;
    private String contactPublicKey;

    public ContactDoc(String contactToken, String contactID, String contactName, String contactPublicKey) {
        this.contactToken = contactToken;
        this.contactID = contactID;
        this.contactName = contactName;
        this.contactPublicKey = contactPublicKey;
    }

    public ContactDoc(String contactID) {
        this.contactToken = "unknwon";
        this.contactID = contactID;
        this.contactName = "unknown";
        this.contactPublicKey = "unknown";
    }

    public ContactDoc(String contactToken,String contactID) {
        this.contactToken = contactToken;
        this.contactID = contactID;
        this.contactName = "unknown";
        this.contactPublicKey = "unknown";
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

    public String getContactPublicKey() {
        return contactPublicKey;
    }

    public void setContactPublicKey(String contactPublicKey) {
        this.contactPublicKey = contactPublicKey;
    }
}
