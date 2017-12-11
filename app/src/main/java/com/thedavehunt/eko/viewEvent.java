package com.thedavehunt.eko;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        Intent i = getIntent();

        //get id of event selected
        String id = i.getStringExtra("id");

        eventNameTxt=(TextView)findViewById(R.id.viewEventName);
        eventDescriptionTxt=(TextView)findViewById(R.id.viewEventDescription);
        eventDateTimeTxt=(TextView)findViewById(R.id.viewEventDateTime);
        eventCategoryTxt=(TextView)findViewById(R.id.viewEventCategory);
        eventCreatorTxt=(TextView)findViewById(R.id.viewEventAuthor);

        retrieveData(id);
    }

    //function that retrieves selected item and displays it to users
    private void retrieveData(String _id){

            final String id=_id;

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

}