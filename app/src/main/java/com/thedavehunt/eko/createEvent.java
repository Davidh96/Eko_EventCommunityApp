package com.thedavehunt.eko;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class createEvent extends Activity {

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference eventRef = rootRef.child("event");

    EditText title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        title =(EditText)findViewById(R.id.edit1);




//        String id = eventRef.push().getKey();
//        eventDoc event1 = new eventDoc(id,"test","admin","test description","Dublin");
//
//        rootRef.child(id).setValue(event1);
    }

    @Override
    protected void onStop() {
        super.onStop();

        String name=title.getText().toString();

        String id = eventRef.push().getKey();
        eventDoc event1 = new eventDoc(id,name,"admin","test description","Dublin");

        rootRef.child(id).setValue(event1);
    }
}
