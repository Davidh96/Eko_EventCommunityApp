package com.thedavehunt.eko;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ViewEvent extends FragmentActivity implements OnMapReadyCallback {

    databaseManager dbm = new databaseManager();
    private String url;


    ListAdapter tempAdapter;
    List<eventMember> members;

    public eventDoc event;

    //get Firebase auth instance
    FirebaseAuth auth= FirebaseAuth.getInstance();
    //get user details
    FirebaseUser user = auth.getCurrentUser();

    private GoogleMap mMap;

    TextView eventNameTxt;
    TextView eventDescriptionTxt;
    TextView eventDateTxt;
    TextView eventTimeTxt;
    TextView eventCategoryTxt;
    TextView eventCreatorTxt;
    TextView memberListTitle;
    ListView memberList;
    ProgressBar loadingCircle;

    FloatingActionButton joinBtn;
    FloatingActionButton leaveBtn;

    String id;
    public static String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        url = getResources().getString(R.string.serverURLrecieve);

        Intent i = getIntent();

        //get id of event selected
        id = i.getStringExtra("id");
        url += id;

        eventNameTxt=(TextView)findViewById(R.id.textEventView);
        eventDescriptionTxt=(TextView)findViewById(R.id.textDescriptionView);
        eventDateTxt=(TextView)findViewById(R.id.textDateView);
        eventTimeTxt=(TextView)findViewById(R.id.textTimeView);
        eventCategoryTxt=(TextView)findViewById(R.id.textCategoryView);
        eventCreatorTxt=(TextView)findViewById(R.id.textCreatorView);
        memberList=(ListView)findViewById(R.id.listMemberView);
        memberListTitle = (TextView)findViewById(R.id.textMemberView);
        joinBtn = (FloatingActionButton)findViewById(R.id.buttonJoinView);
        leaveBtn = (FloatingActionButton)findViewById(R.id.buttonLeaveView);
        loadingCircle=(ProgressBar)findViewById(R.id.loadingCircle);

        loadingCircle.setVisibility(View.VISIBLE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        //display user loation
        mMap.setMyLocationEnabled(true);
        //display button to show user location
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        //get event data
        retrieveData();

    }

    public void retrieveData(){

        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingCircle.setVisibility(View.INVISIBLE);

                        try {

                            //getting the whole json object from the response
                            JSONObject eventObj = new JSONObject(response);


                            //place data into an eventDoc
                            event = new eventDoc(eventObj.getString("id"),eventObj.getString("eventName"),eventObj.getString("eventAuthor"),eventObj.getString("eventAuthorID"),
                                    eventObj.getString("eventDescription"),eventObj.getString("eventCategory"),eventObj.getString("eventLocation"),eventObj.getString("eventDate"),
                                    eventObj.getString("eventTime"));

                            //get members list
                            JSONArray members = eventObj.getJSONArray("members");

                            //place each member into event member
                            for(int i=0;i<members.length();i++){
                                JSONObject mem = members.getJSONObject(i);
                                eventMember member = new eventMember(mem.getString("id"),mem.getString("name"));
                                event.addMembers(member);

                            }

                            //display data
                            setData();


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
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    //display dat to user
    public void setData(){

        //place event info into text views
        eventNameTxt.setText(event.getEventName());
        eventDescriptionTxt.setText(event.getEventDescription());
        eventDateTxt.setText(event.getEventDate());
        eventTimeTxt.setText(event.getEventTime());
        eventCategoryTxt.setText(event.getEventCategory());
        eventCreatorTxt.setText(event.getEventAuthor());

        members = event.getMembers();

        memberListTitle.setText(getResources().getString(R.string.memberListTitle) + " (" + members.size()  + ")");

        int check=0;

        //check if user is already part of the members
        for(int j =0;j<members.size();j++){
            if(user.getUid().equals(members.get(j).getId())){
                check++;
            }
        }

        //if user is member
        if(check>0){
            joinBtn.hide();
            leaveBtn.show();
        }
        else{
            joinBtn.show();
            leaveBtn.hide();
        }

        //initialise adapter
        tempAdapter = new memberListAdapter(ViewEvent.this,members);

        memberList.setAdapter(tempAdapter);

        ViewEvent.this.location=event.getEventLocation();

        //seperator for lat and long
        int commaPos = location.indexOf(",");

        LatLng eventLoc = new LatLng(Double.parseDouble(location.substring(0,commaPos)),Double.parseDouble(location.substring(commaPos+1)));
        mMap.addMarker(new MarkerOptions().position(eventLoc).title(event.getEventName()));
        //set camera position and zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLoc,15));

        //if the current user is the creator of this event, show event editing tools
        if(event.getEventAuthorID().equals(user.getUid())) {
            //create fragment for tools
            FragmentManager fragmentManager = getFragmentManager();

            viewEventToolsFragment frag1 = new viewEventToolsFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            //show fragment
            fragmentTransaction.add(R.id.edit_tools_layout_container, frag1);
            fragmentTransaction.show(frag1);
            fragmentTransaction.commit();
        }
    }

    //allow editing of events
    public void editEvent(View v){
        Intent createEvent = new Intent(getApplicationContext(),CreateEvent.class);
        createEvent.putExtra("id",id);
        startActivity(createEvent);
    }

    //add member to event
    public void joinEvent(View v){

        //create alert box to ask user if they wish to join event
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //alert title
        alert.setTitle("Join Event")
                //alert message
                .setMessage("Do you want to join the '" + eventNameTxt.getText() + "' event?")
                //if user clicks yes
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        eventMember member = new eventMember(user.getUid(),user.getDisplayName());

                        dbm.addEventMember(id,member);

                        joinBtn.hide();
                        leaveBtn.show();

                        Toast.makeText(getApplicationContext(),"Joined '" + event.eventName + "' Event",Toast.LENGTH_SHORT).show();

                        retrieveData();
                    }

                })
                //if user does not wish to join
                .setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();

    }

    public void leaveEvent(View v){
        if(!user.getUid().equals(event.eventAuthorID)) {
            //create alert box to ask user if they wish to leave the event
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            //alert title
            alert.setTitle("Leave Event")
                    //alert message
                    .setMessage("Do you want to leave the '" + eventNameTxt.getText() + "' event?")
                    //if user clicks yes
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            eventMember member = new eventMember(user.getUid(), user.getDisplayName());

                            dbm.removeEventMember(id, member);

                            joinBtn.show();
                            leaveBtn.hide();

                            Toast.makeText(getApplicationContext(), "Left '" + event.eventName + "' Event", Toast.LENGTH_SHORT).show();

                            retrieveData();

                        }

                    })
                    //if user does not wish to post
                    .setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();


        }
        else{
            Toast.makeText(getApplicationContext(),"You cannot leave event, you must delete the event", Toast.LENGTH_LONG).show();
        }
    }

    //delete events
    public void deleteEvent(View v){
        //create alert box to ask user if they wish to post to facebook
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //alert title
        alert.setTitle("Delete Event")
                //alert message
                .setMessage("Are you sure you want to delete this '" + eventNameTxt.getText() + "' event?")
                //if user clicks yes
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //delete currently selected event
                        dbm.deleteEvent(id,ViewEvent.this);
                        finish();

                    }

                })
                //if user does not wish to post
                .setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

}