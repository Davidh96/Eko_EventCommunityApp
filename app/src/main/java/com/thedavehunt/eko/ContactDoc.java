package com.thedavehunt.eko;

//used to store contact details
public class ContactDoc {

    private String contactToken;//stores contact token
    private String contactID;//stores contact id
    private String contactName;//stores contact name
    private String contactPublicKey;//stores contact public key

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

    //getters and setters
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
