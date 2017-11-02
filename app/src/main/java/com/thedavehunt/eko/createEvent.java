package com.thedavehunt.eko;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class createEvent extends Activity {

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference eventRef = rootRef.child("event");

    EditText nameEdt;
    EditText descriptionEdt;
    Spinner categorySpin;

    String name="";
    String description="";
    String category="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        nameEdt =(EditText)findViewById(R.id.editName);
        descriptionEdt =(EditText)findViewById(R.id.editDescription);
        categorySpin = (Spinner) findViewById(R.id.spinnerCategory);


    // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
    // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Apply the adapter to the spinner
        categorySpin.setAdapter(adapter);

        categorySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
                Toast.makeText(createEvent.this,category,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //FAB, used to allow creation of new events
        FloatingActionButton CreateFab = (FloatingActionButton) findViewById(R.id.fabCreate);
        CreateFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                saveEvent();
                finish();

            }
        });

        //FAB, used to cancel creation of new events
        FloatingActionButton CancelFab = (FloatingActionButton) findViewById(R.id.fabCancel);
        CancelFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                finish();
            }
        });
    }

    //method for saving new events to the Firebase database
    protected void saveEvent(){
        //get event name
        name=nameEdt.getText().toString();
        //get event description
        description=descriptionEdt.getText().toString();

        String id = eventRef.push().getKey();
        eventDoc event1 = new eventDoc(id,name,"admin",description,category,"Dublin");

        rootRef.child(id).setValue(event1);
    }

    @Override
    protected void onStop() {
        super.onStop();


    }
}
