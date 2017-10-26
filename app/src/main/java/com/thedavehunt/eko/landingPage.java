package com.thedavehunt.eko;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
    DatabaseReference eventRef = rootRef.child("event");
    ArrayList<String> list2 = new ArrayList();
    List<eventDoc> eventList;
    ListAdapter tempAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        list2.add("test");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                Toast.makeText(landingPage.this,"Clicked", Toast.LENGTH_SHORT).show();

            }
        });


        eventList= new ArrayList<eventDoc>();

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.clear();

                for(DataSnapshot eventSnapshop: dataSnapshot.getChildren()){
                    Toast.makeText(landingPage.this,""+eventSnapshop.toString(),Toast.LENGTH_SHORT).show();
                    eventDoc evnt = eventSnapshop.getValue(eventDoc.class);
                    //Toast.makeText(landingPage.this,evnt.getEventName(),Toast.LENGTH_SHORT).show();
                   eventList.add(evnt);
                }
                tempAdapter = new landingListAdapter(landingPage.this,eventList);
                //ArrayAdapter ww = new ArrayAdapter(landingPage.this,android.R.layout.simple_list_item_1,list2);

                ListView list = (ListView)findViewById(R.id.list1);
                list.setAdapter(tempAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
