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
//                Intent createEvent = new Intent(landingPage.this,createEvent.class);
//                startActivity(createEvent);
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


        rootRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                eventDoc event = dataSnapshot.getValue(eventDoc.class);
//                for(DataSnapshot eventy : dataSnapshot.getChildren()){
//                    eventList.add(event);
//                }
//
//                //list2.add(strin);
//                //Toast.makeText(landingPage.this,strin,Toast.LENGTH_SHORT);
//                tempAdapter = new landingListAdapter(landingPage.this,eventList);
//                //ArrayAdapter ww = new ArrayAdapter(landingPage.this,android.R.layout.simple_list_item_1,list2);
//
//                ListView list = (ListView)findViewById(R.id.list1);
//                list.setAdapter(tempAdapter);
//
//
//                //tempAdapter.notifyAll();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        Toast.makeText(landingPage.this,""+tempin, Toast.LENGTH_SHORT).show();
//        final String[] temp = {"event1","event2","event3",tempin};
//
//        //ListAdapter tempAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,temp);
//        tempAdapter = new landingListAdapter(this,temp);
//
//
//        ListView list = (ListView)findViewById(R.id.list1);
//        list.setAdapter(tempAdapter);
//
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(landingPage.this,""+temp[position], Toast.LENGTH_SHORT).show();
//            }
//        });
    }


}
