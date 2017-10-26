package com.thedavehunt.eko;

/**
 * Created by david on 25/10/17.
 */

public class eventDoc {
    String id;
    String eventName;
    String eventAuthor;
    String eventDescription;
    String eventCategory;
    String eventLocation;

    public eventDoc() {
    }

    public eventDoc(String id, String eventName, String eventAuthor, String eventDescription,String eventCategory, String eventLocation) {
        this.id = id;
        this.eventName = eventName;
        this.eventAuthor = eventAuthor;
        this.eventDescription = eventDescription;
        this.eventCategory = eventCategory;
        this.eventLocation = eventLocation;
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
}
