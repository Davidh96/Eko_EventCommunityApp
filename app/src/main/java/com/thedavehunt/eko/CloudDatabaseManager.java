package com.thedavehunt.eko;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by david on 11/12/17.
 */

public class CloudDatabaseManager {

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference eventRef = rootRef.child("event");
    //get Firebase auth instance
    FirebaseAuth auth= FirebaseAuth.getInstance();
    //get user details
    FirebaseUser user = auth.getCurrentUser();

    private String url = "http://188.166.98.100//";

    public ArrayList<EventDoc> eventList;

    public EventDoc event;

    public String contactToken;


    public void createEvent(EventDoc event){
        this.event=event;

        String id = event.getId();

        //check if this is an edited event
        if (id == null) {
            //if new event, create new key
            id = eventRef.push().getKey();
        }

        event.setId(id);

        event.addMembers(new EventMember(user.getUid()));

        //push event to cloud database
        rootRef.child("events").child(id).setValue(event);
        //give rating of 5 from the creator
        rateEvent(user.getUid(),id,5);

        Toast.makeText(getApplicationContext(),"Event Created",Toast.LENGTH_SHORT).show();
    }

    //give selected event a rating from a user
    public void rateEvent(String userID, String eventID,float rating){

        rootRef.child("users").child(userID).child("rated").child(eventID).child("rating").setValue(rating);

    }

    public void updateUserDisplayName(String userID,String Dsiplayname){
        rootRef.child("users").child(userID).child("DisplayName").setValue(Dsiplayname);
    }

    //send updated token to firebase db
    public void updateToken(String userID,String userToken){
        rootRef.child("users").child(userID).child("Token").setValue(userToken);
        Log.d("Updating Token","hi");
    }

    //send updated token to firebase db
    public void updatePublicKey(String userID,String publicKey){
        rootRef.child("users").child(userID).child("PublicKey").setValue(publicKey);
    }

    //add member to event
    public void addEventMember(String eventId,EventMember member){

        final EventMember member1 = member;
        final String id =eventId;

        rootRef.child("events").child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                //get event class
                event = snapshot.getValue(EventDoc.class);
                event.addMembers(member1);
                rootRef.child("events").child(id).setValue(event);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    //remove member from event
    public void removeEventMember(String eventId, EventMember member){
        final EventMember member1 = member;
        final String id =eventId;

        rootRef.child("events").child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                //get event class
                CloudDatabaseManager.this.event = snapshot.getValue(EventDoc.class);
                event.removeMembers(member1);
                rootRef.child("events").child(id).setValue(event);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //delete a selected event
    public void deleteEvent(String id,Context con){
        //((Activity)con).finish();

        rootRef.child("events").child(id).removeValue();

    }


//    public ArrayList<eventDoc> retrieveAllEvents(){
//        eventList = new ArrayList<eventDoc>();
//    //creating a string request to send request to the url
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        //hiding the progressbar after completion
//                        //progressBar.setVisibility(View.INVISIBLE);
//
//                        try {
//
//                            //getting the whole json object from the response
//                            JSONObject obj = new JSONObject(response);
//                            //titleText.setText(obj.toString());
//
//                            //we have the array named hero inside the object
//                            //so here we are getting that json array
//                            JSONArray result = obj.getJSONArray("events");
//
//
//                            //now looping through all the elements of the json array
//                            for (int i = 0; i < result.length(); i++) {
//
//                                //JSONObject obj1 = new JSONObject(test4);
//                                //JSONArray result = obj1.getJSONArray("events");
//                                String startString = "{\"-";
//                                //String temp = result.toString();
//                                String temp = result.getJSONObject(i).toString();
//                                int num = temp.indexOf(startString);
//                                String endString ="\"";
//                                int num1 = temp.indexOf(endString,temp.indexOf(endString)+1);
//
//                                String key = temp.substring(num+startString.length(),num1);
//                                JSONObject eventObj = result.getJSONObject(i).getJSONObject("-" + key);
//                                temp = eventObj.getString("eventDescription");
//
//
//                                eventDoc event = new eventDoc(eventObj.getString("id"),eventObj.getString("eventName"),eventObj.getString("eventAuthor"),eventObj.getString("eventAuthorID"),
//                                        eventObj.getString("eventDescription"),eventObj.getString("eventCategory"),eventObj.getString("eventLocation"),eventObj.getString("eventDate"),
//                                        eventObj.getString("eventTime"));
//
//                                databaseManager.this.eventList.add(event);
//
//
//                            }
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        //displaying the error in toast if occurrs
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//        //creating a request queue
//        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//
//        //adding the string request to request queue
//        requestQueue.add(stringRequest);
//
//        Toast.makeText(getApplicationContext(),"" + eventList.toString(),Toast.LENGTH_LONG).show();
//
//        return eventList;
//    }

//    public void readEvent(String id){
//
//        Toast.makeText(getApplicationContext(),id,Toast.LENGTH_LONG).show();
//
//
//        rootRef.child(id).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//
//                //get event class
//                databaseManager.this.event = snapshot.getValue(eventDoc.class);
//
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//
//        Toast.makeText(getApplicationContext(),databaseManager.this.event.getEventName(),Toast.LENGTH_LONG).show();
//
//        //return event;
//    }



    public void getContactToken(final String id){

        Cursor results;
        final LocalDatabaseManager dbm;

        dbm = new LocalDatabaseManager(getApplicationContext());
        SQLiteDatabase db = dbm.getReadableDatabase();


        rootRef.child("users").child(id).child("Token").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                //get event class

                String contactToken= snapshot.getValue().toString();
                Log.d("Getting token",contactToken);
                LocalDatabaseManager db = new LocalDatabaseManager(getApplicationContext());
                ContactDoc contact = new ContactDoc(contactToken,id);
                db.updateContact(contact);


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    public void getContactKey(final String id){

        Cursor results;
        final LocalDatabaseManager dbm;

        dbm = new LocalDatabaseManager(getApplicationContext());
        SQLiteDatabase db = dbm.getReadableDatabase();


        rootRef.child("users").child(id).child("PublicKey").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                LocalDatabaseManager dbm = new LocalDatabaseManager(getApplicationContext());
                Cursor result = dbm.retrieveContact(dbm.getWritableDatabase(),id);

                ContactDoc contact=null;

                String name=null;
                String token=null;

                while(result.moveToNext()){
                    //cursor.getString(cursor.getColumnIndex("TokenValue"));
                    name = result.getString(result.getColumnIndex("contactName"));
                    token = result.getString(result.getColumnIndex("contactToken"));
                }

                //get event class

                String contactKey= snapshot.getValue().toString();
                contact = new ContactDoc(token,id,name,contactKey);
                //contact.setContactPublicKey(contactKey);


                dbm.updateContact(contact);


                Log.d("contactID",contact.getContactID());

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


}
