package com.thedavehunt.eko;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by david on 25/10/17.
 */

public class eventDoc {
    String id;
    String eventName;
    String eventAuthor;
    String eventAuthorID;
    String eventDescription;
    String eventCategory;
    String eventLocation;
    String eventDate;
    String eventTime;

    //eventMember member;

    //DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();


    public eventDoc() {
    }

    public eventDoc(String id, String eventName, String eventAuthor, String eventAuthorID,  String eventDescription,String eventCategory, String eventLocation, String eventDate, String eventTime) {
        this.id = id;
        this.eventName = eventName;
        this.eventAuthor = eventAuthor;
        this.eventAuthorID=eventAuthorID;
        this.eventDescription = eventDescription;
        this.eventCategory = eventCategory;
        this.eventLocation = eventLocation;
        this.eventDate=eventDate;
        this.eventTime=eventTime;

        //DatabaseReference usersRef = rootRef.child("users");
//        DatabaseReference eventRef = rootRef.child("event");
//
//        member = new eventMember("1","david");
//
////        Map<String, eventMember> users = new HashMap<>();
////        users.put("alanisawesome", new eventMember("June 23, 1912", "Alan Turing"));
////        users.put("gracehop", new eventMember("December 9, 1906", "Grace Hopper"));
//
//        eventRef.setValue("hello");


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventAuthor() {
        return eventAuthor;
    }

    public void setEventAuthor(String eventAuthor) {
        this.eventAuthor = eventAuthor;
    }


    public String getEventAuthorID() {
        return eventAuthorID;
    }

    public void setEventAuthorID(String eventAuthorID) {
        this.eventAuthorID = eventAuthorID;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }
}
