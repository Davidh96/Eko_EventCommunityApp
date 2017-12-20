package com.thedavehunt.eko;

import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    List<eventMember> members = new ArrayList<eventMember>();

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
//        eventMember mem = new eventMember("test","test");
//        members.add(mem);
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

    public List<eventMember> getMembers() {
        return members;
    }

    public void setMembers(List<eventMember> members) {
        this.members = members;
    }

    public void addMembers(eventMember member) {
        this.members.add(member);
    }

    public void removeMembers(eventMember member){
        for(int i=0;i < members.size();i++){
            if(members.get(i).getId().equals(member.getId())){
                members.remove(i);
            }
        }


    }

}
