package com.thedavehunt.eko;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import java.util.List;

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

    private String url = "http://188.166.98.100//";

    public ArrayList<eventDoc> eventList;



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


        //String location = event.getEventLocation();
//        int ind = location.indexOf(",");
//
//        String lat = location.substring(0,ind);
//        String lon = location.substring(ind+1);
//        lat= lat+ "00";
//        lon = lon+"00";
//        int latD = lat.indexOf(".");
//        int lonD = lon.indexOf(".");
//
//        lat = lat.substring(0,latD+3);
//        lon = lon.substring(0,lonD+3);
//
//        lat = lat.replace(".","_");
//        lon = lon.replace(".","_");


        //Toast.makeText(getApplicationContext(),lat + " Hello "+ lon,Toast.LENGTH_SHORT).show();


//        Double lat = Double.parseDouble(location.substring(0,ind));
//        Double lon = Double.parseDouble(location.substring(ind+1));
//        lat = (double)Math.round(lat*100)/100;
//        Toast.makeText(getApplicationContext(),lat + " Hello "+ lon,Toast.LENGTH_SHORT).show();

        //push event to cloud database
        rootRef.child("events").child(id).setValue(event);
        rateEvent(user.getUid(),id,5);


        //set creator as an event member
//        eventMember creator = new eventMember(user.getUid(),user.getDisplayName());
//        addEventMember(id,creator);

        Toast.makeText(getApplicationContext(),"Event Created",Toast.LENGTH_SHORT).show();
    }

    public void rateEvent(String userID, String eventID,float rating){

        rootRef.child("users").child(userID).child("rated").child(eventID).child("rating").setValue(rating);

    }

    public void updateToken(String userID,String userToken){
        rootRef.child("users").child(userID).child("Token").setValue(userToken);
    }

    //add member to event
    public void addEventMember(String eventId,eventMember member){

        final eventMember member1 = member;
        final String id =eventId;

        rootRef.child("events").child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                //get event class
                databaseManager.this.event = snapshot.getValue(eventDoc.class);
                event.addMembers(member1);
                rootRef.child("events").child(id).setValue(event);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void removeEventMember(String eventId, eventMember member){
        final eventMember member1 = member;
        final String id =eventId;

        rootRef.child("events").child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                //get event class
                databaseManager.this.event = snapshot.getValue(eventDoc.class);
                event.removeMembers(member1);
                rootRef.child("events").child(id).setValue(event);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    public ArrayList<eventDoc> retrieveAllEvents(){
        eventList = new ArrayList<eventDoc>();
    //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        //progressBar.setVisibility(View.INVISIBLE);

                        try {

                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);
                            //titleText.setText(obj.toString());

                            //we have the array named hero inside the object
                            //so here we are getting that json array
                            JSONArray result = obj.getJSONArray("events");


                            //now looping through all the elements of the json array
                            for (int i = 0; i < result.length(); i++) {

                                //JSONObject obj1 = new JSONObject(test4);
                                //JSONArray result = obj1.getJSONArray("events");
                                String startString = "{\"-";
                                //String temp = result.toString();
                                String temp = result.getJSONObject(i).toString();
                                int num = temp.indexOf(startString);
                                String endString ="\"";
                                int num1 = temp.indexOf(endString,temp.indexOf(endString)+1);

                                String key = temp.substring(num+startString.length(),num1);
                                JSONObject eventObj = result.getJSONObject(i).getJSONObject("-" + key);
                                temp = eventObj.getString("eventDescription");


                                eventDoc event = new eventDoc(eventObj.getString("id"),eventObj.getString("eventName"),eventObj.getString("eventAuthor"),eventObj.getString("eventAuthorID"),
                                        eventObj.getString("eventDescription"),eventObj.getString("eventCategory"),eventObj.getString("eventLocation"),eventObj.getString("eventDate"),
                                        eventObj.getString("eventTime"));

                                databaseManager.this.eventList.add(event);


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //adding the string request to request queue
        requestQueue.add(stringRequest);

        Toast.makeText(getApplicationContext(),"" + eventList.toString(),Toast.LENGTH_LONG).show();

        return eventList;
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

        rootRef.child("events").child(id).removeValue();

    }
}
