package com.thedavehunt.eko;

/**
 * Created by david on 19/12/17.
 */

public class eventMember {
    String id;
    String name;

    public eventMember(String id, String name){
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
