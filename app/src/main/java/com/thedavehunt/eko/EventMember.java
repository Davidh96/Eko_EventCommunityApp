package com.thedavehunt.eko;

/**
 * Created by david on 19/12/17.
 */

public class EventMember {
    String id;
    String name;

    public  EventMember(){
    }

    public EventMember(String id, String name){
        this.id=id;
        this.name=name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
