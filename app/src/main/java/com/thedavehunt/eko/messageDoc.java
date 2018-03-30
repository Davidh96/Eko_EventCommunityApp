package com.thedavehunt.eko;

public class messageDoc {

    private String senderID;
    private String senderToken;
    private String senderName;
    private  String senderMessage;

    public messageDoc(String senderID, String senderToken, String senderName, String senderMessage){
        this.senderID = senderID;
        this.senderToken=senderToken;
        this.senderName=senderName;
        this.senderMessage=senderMessage;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderToken() {
        return senderToken;
    }

    public void setSenderToken(String senderToken) {
        this.senderToken = senderToken;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderMessage() {
        return senderMessage;
    }

    public void setSenderMessage(String senderMessage) {
        this.senderMessage = senderMessage;
    }
}
