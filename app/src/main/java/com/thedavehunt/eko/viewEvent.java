package com.thedavehunt.eko;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class viewEvent extends FragmentActivity implements OnMapReadyCallback {

    databaseManager dbm = new databaseManager();

    ListAdapter tempAdapter;
    List<eventMember> members;

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    //get Firebase auth instance
    FirebaseAuth auth= FirebaseAuth.getInstance();
    //get user details
    FirebaseUser user = auth.getCurrentUser();

    private GoogleMap mMap;
    eventDoc event;

    TextView eventNameTxt;
    TextView eventDescriptionTxt;
    TextView eventDateTxt;
    TextView eventTimeTxt;
    TextView eventCategoryTxt;
    TextView eventCreatorTxt;
    ListView memberList;

    String id;
    public static String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        Intent i = getIntent();

        //get id of event selected
        id = i.getStringExtra("id");

        eventNameTxt=(TextView)findViewById(R.id.viewEventName);
        eventDescriptionTxt=(TextView)findViewById(R.id.viewEventDescription);
        eventDateTxt=(TextView)findViewById(R.id.viewEventDate);
        eventTimeTxt=(TextView)findViewById(R.id.viewEventTime);
        eventCategoryTxt=(TextView)findViewById(R.id.viewEventCategory);
        eventCreatorTxt=(TextView)findViewById(R.id.viewEventAuthor);
        memberList=(ListView)findViewById(R.id.list2);

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


        //retrieve data
        rootRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                //get event class
                viewEvent.this.event = snapshot.getValue(eventDoc.class);

                //place event info into text views
                eventNameTxt.setText(event.getEventName());
                eventDescriptionTxt.setText(event.getEventDescription());
                eventDateTxt.setText(event.getEventDate());
                eventTimeTxt.setText(event.getEventTime());
                eventCategoryTxt.setText(event.getEventCategory());
                eventCreatorTxt.setText(event.getEventAuthor());

                members = event.getMembers();

                //initialise adapter
                tempAdapter = new memberListAdapter(viewEvent.this,members);

                memberList.setAdapter(tempAdapter);

                viewEvent.this.location=event.getEventLocation();

                //seperator for lat and long
                int commaPos = location.indexOf(",");

                LatLng eventLoc = new LatLng(Double.parseDouble(location.substring(0,commaPos)),Double.parseDouble(location.substring(commaPos+1)));
                mMap.addMarker(new MarkerOptions().position(eventLoc).title(event.getEventName()));
                //set camera position and zoom
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLoc,15));

                //if the current user is the creator of this event, show event editing tools
                if(event.getEventAuthorID().equals(user.getUid())){
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
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    //add member to event
    public void joinEvent(View v){
        int check=0;

        //check if user is already part of the members
        for(int i =0;i<members.size();i++){
            if(user.getUid()==members.get(i).getId()){
                check++;
                Toast.makeText(getApplicationContext(),user.getUid() + " , " + members.get(i).getId(),Toast.LENGTH_SHORT).show();
            }
        }

        //if user is member
        if(check>0){
            Toast.makeText(getApplicationContext(),"You have already joined this event",Toast.LENGTH_SHORT).show();
        }
        //if not member
        else{

            //create alert box to ask user if they wish to post to facebook
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

                            Toast.makeText(getApplicationContext(),"Joined '" + event.eventName + "' Event",Toast.LENGTH_SHORT).show();
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

    //allow editing of events
    public void editEvent(View v){
        Intent createEvent = new Intent(getApplicationContext(),createEvent.class);
        createEvent.putExtra("id",id);
        startActivity(createEvent);
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
                        dbm.deleteEvent(id,viewEvent.this);
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