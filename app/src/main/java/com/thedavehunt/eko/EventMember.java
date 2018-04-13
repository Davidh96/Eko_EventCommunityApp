package com.thedavehunt.eko;

/**
 * Created by david on 19/12/17.
 */

//stores information about event members
public class EventMember {
    String id;
    String name;
    String token;
    String key;

    public  EventMember(){
    }

    public EventMember(String id){
        this.id=id;

    }


    //getters and setters
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
