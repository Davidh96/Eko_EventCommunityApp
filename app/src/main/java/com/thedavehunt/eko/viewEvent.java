package com.thedavehunt.eko;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class viewEvent extends Activity {

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    TextView eventNameTxt;
    TextView eventDescriptionTxt;
    TextView eventDateTimeTxt;
    TextView eventCategoryTxt;
    TextView eventCreatorTxt;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        Intent i = getIntent();

        //get id of event selected
        id = i.getStringExtra("id");

        eventNameTxt=(TextView)findViewById(R.id.viewEventName);
        eventDescriptionTxt=(TextView)findViewById(R.id.viewEventDescription);
        eventDateTimeTxt=(TextView)findViewById(R.id.viewEventDateTime);
        eventCategoryTxt=(TextView)findViewById(R.id.viewEventCategory);
        eventCreatorTxt=(TextView)findViewById(R.id.viewEventAuthor);

        retrieveData();
    }

    //function that retrieves selected item and displays it to users
    private void retrieveData(){



            rootRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                //get event class
                eventDoc event = snapshot.getValue(eventDoc.class);

                //place event info into text views
                eventNameTxt.setText(event.getEventName());
                eventDescriptionTxt.setText(event.getEventDescription());
                eventCategoryTxt.setText(event.getEventCategory());
                eventCreatorTxt.setText(event.getEventAuthor());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void editEvent(View v){
        Intent createEvent = new Intent(getApplicationContext(),createEvent.class);
        createEvent.putExtra("id",id);
        startActivity(createEvent);
    }

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
                        rootRef.child(id).removeValue();
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