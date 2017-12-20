package com.thedavehunt.eko;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    //get Firebase auth instance
    FirebaseAuth auth= FirebaseAuth.getInstance();
    //get user details
    FirebaseUser user = auth.getCurrentUser();



    public eventDoc event;

    public void createEvent(eventDoc event){
        this.event=event;
        String id = event.getId();

        //check if this is an edited event
        if (id == null) {
            //if new event, create new key
            id = eventRef.push().getKey();
        }

        event.setId(id);

        event.addMembers(new eventMember(user.getUid(),user.getDisplayName()));

        //push event to cloud database
        rootRef.child(id).setValue(event);

        //set creator as an event member
//        eventMember creator = new eventMember(user.getUid(),user.getDisplayName());
//        addEventMember(id,creator);

        Toast.makeText(getApplicationContext(),"Event Created",Toast.LENGTH_SHORT).show();
    }

    //add member to event
    public void addEventMember(String eventId,eventMember member){

        final eventMember member1 = member;
        final String id =eventId;

        rootRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                //get event class
                databaseManager.this.event = snapshot.getValue(eventDoc.class);
                event.addMembers(member1);
                rootRef.child(id).setValue(event);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void readEvent(String id){

        Toast.makeText(getApplicationContext(),id,Toast.LENGTH_LONG).show();


        rootRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                //get event class
                databaseManager.this.event = snapshot.getValue(eventDoc.class);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        Toast.makeText(getApplicationContext(),databaseManager.this.event.getEventName(),Toast.LENGTH_LONG).show();

        //return event;
    }

    public void deleteEvent(String id,Context con){
        //((Activity)con).finish();

        rootRef.child(id).removeValue();

    }
}
