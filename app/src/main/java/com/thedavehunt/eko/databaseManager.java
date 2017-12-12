package com.thedavehunt.eko;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by david on 11/12/17.
 */

public class databaseManager {
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference eventRef = rootRef.child("event");

    static eventDoc event;

    public void createEvent(eventDoc event){
        this.event=event;
        String id = event.getId();

        //check if this is an edited event
        if (id == null) {
            //if new event, create new key
            id = eventRef.push().getKey();
        }

        event.setId(id);

        //push event to cloud database
        rootRef.child(id).setValue(event);
        Toast.makeText(getApplicationContext(),"Event Created",Toast.LENGTH_SHORT).show();
    }

    public eventDoc readEvent(String id){

        Toast.makeText(getApplicationContext(),id,Toast.LENGTH_LONG).show();


        rootRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                //get event class
                databaseManager.event = snapshot.getValue(eventDoc.class);


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        Toast.makeText(getApplicationContext(),event.getEventName(),Toast.LENGTH_LONG).show();

        return event;
    }

    public void deleteEvent(String id,Context con){
        //((Activity)con).finish();

        rootRef.child(id).removeValue();

    }
}
