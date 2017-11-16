package com.thedavehunt.eko;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class landingPage extends Activity {

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    List<eventDoc> eventList;
    ListAdapter tempAdapter;

    FirebaseAuth auth;

    Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        logoutBtn = (Button) findViewById(R.id.logoutBtn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                finish();
            }
        });

        //FAB, used to allow creation of new events
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                Intent createevent = new Intent(landingPage.this,createEvent.class);
                startActivity(createevent);

            }
        });


        eventList= new ArrayList<eventDoc>();

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.clear();

                for(DataSnapshot eventSnapshop: dataSnapshot.getChildren()){
                    //retrieve data from db and place it in a eventDoc structure
                    eventDoc evnt = eventSnapshop.getValue(eventDoc.class);
                    //add event to list
                    eventList.add(evnt);
                }
                //initialise adapter
                tempAdapter = new landingListAdapter(landingPage.this,eventList);

                ListView list = (ListView)findViewById(R.id.list1);
                //set adapter for list view
                list.setAdapter(tempAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
