package com.thedavehunt.eko;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Context.LOCATION_SERVICE;

public class createEvent extends AppCompatActivity {

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference eventRef = rootRef.child("event");

    CallbackManager callbackManager;

    EditText nameEdt;
    EditText descriptionEdt;
    Spinner categorySpin;
    ShareDialog shareDialog;


    String name = "";
    String description = "";
    String category = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        nameEdt = (EditText) findViewById(R.id.editName);
        descriptionEdt = (EditText) findViewById(R.id.editDescription);
        categorySpin = (Spinner) findViewById(R.id.spinnerCategory);

        //dialog for facebook sharing
        shareDialog = new ShareDialog(this);


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


            }
        });

        //FAB, used to cancel creation of new events
        FloatingActionButton CancelFab = (FloatingActionButton) findViewById(R.id.fabCancel);
        CancelFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                Toast.makeText(getApplicationContext(),"Cancelled",Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }

    //for posting to facebook
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //method for saving new events to the Firebase database
    protected void saveEvent(){
        boolean saved=true;
        //get event name
        name=nameEdt.getText().toString();
        //get event description
        description=descriptionEdt.getText().toString();

        if(name.isEmpty() || description.isEmpty()){
            Toast.makeText(getApplicationContext(),"Please fill in all information",Toast.LENGTH_SHORT).show();
            saved=false;
        }
        else if(description.length()<25){
            Toast.makeText(getApplicationContext(),"Description must be atleast 25 characters long",Toast.LENGTH_SHORT).show();
            saved=false;
        }

        if(saved) {
            String id = eventRef.push().getKey();

            //get Firebase auth instance
            FirebaseAuth auth= FirebaseAuth.getInstance();
            //get user details
            FirebaseUser user = auth.getCurrentUser();
            //retrieve users facebook name
            String author = user.getDisplayName();

            // create an event
            eventDoc event = new eventDoc(id, name, author, description, category, "Dublin");

            //push event to cloud database
            rootRef.child(id).setValue(event);
            Toast.makeText(getApplicationContext(),"Event Created",Toast.LENGTH_SHORT).show();

            //create alert box to ask user if they wish to post to facebook
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            //alert title
            alert.setTitle("Share this Event!")
                    //alert message
                    .setMessage("Want to post this event on Facebook?")
                    //if user clicks yes
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //setup posting event to facebook
                            if (ShareDialog.canShow(ShareLinkContent.class)) {
                                //set post details
                                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                        .setContentTitle("Test")
                                        .setContentDescription(
                                                "Test Description")
                                        .setQuote(name + ": " +description)
                                        .setContentUrl(Uri.parse("http://facebook.com"))
                                        .build();

                                //display post information
                                shareDialog.show(linkContent);
                            }
                            finish();

                        }

                    })
                    //if user does not wish to post
                    .setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .show();

        }

    }

}
