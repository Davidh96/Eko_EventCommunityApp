package com.thedavehunt.eko;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.MapView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.LOCATION_SERVICE;

public class createEvent extends Activity {

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    LocationManager locationManager;
    LocationListener locationListener;

    CallbackManager callbackManager;

    EditText nameEdt;
    EditText descriptionEdt;
    Spinner categorySpin;
    DatePicker dateEdt;
    TimePicker timeEdt;

    Button timeToggle;
    Button dateToggle;

    ShareDialog shareDialog;


    String name = "";
    String description = "";
    String category = "";
    String location="";
    String date="";
    String time="";
    String id;

    boolean toggle=false;

    public static double locLong;
    public static double locLat;

    ArrayAdapter<CharSequence> adapter;
    databaseManager dbm = new databaseManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);



        Intent i = getIntent();
        //get id of event selected
        id = i.getStringExtra("id");



        nameEdt = (EditText) findViewById(R.id.createEditName);
        descriptionEdt = (EditText) findViewById(R.id.createEditDescription);
        categorySpin = (Spinner) findViewById(R.id.createSpinnerCategory);

        dateToggle = (Button)findViewById(R.id.createButtonDate);
        timeToggle = (Button)findViewById(R.id.createButtonTime);



        //dialog for facebook sharing
        shareDialog = new ShareDialog(this);


        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        categorySpin.setAdapter(adapter);

        if(id!=null){
            retrieveData();
        }

        categorySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();

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

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            //when location is updated
            @Override
            public void onLocationChanged(Location location) {
                locLong = location.getLongitude();
                locLat = location.getLatitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            //if gps is disabled
            @Override
            public void onProviderDisabled(String s) {
                Intent _intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(_intent);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.INTERNET
                }, 10);
                return;
            }
        } else {
            getLocation();
        }

        getLocation();


    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        //if location was set correctly
        if(resultCode == Activity.RESULT_OK){
            location = "" + data.getDoubleExtra("eLat",0) + "," + data.getDoubleExtra("eLong",0);
        }
        else{
            location = "" + locLat + "," + locLong;
        }
    }

    //method for saving new events to the Firebase database
    protected void saveEvent(){
        boolean saved=true;
        //get event name
        name=nameEdt.getText().toString();
        //get event description
        description=descriptionEdt.getText().toString();

        //date="test";
        //date = "" + dateEdt.getDayOfMonth() + "-" + dateEdt.getMonth() + "-" + dateEdt.getYear();

        //time="test1";
        //time = "" + timeEdt.getHour() + ":" + timeEdt.getMinute();
        Toast.makeText(getApplicationContext(),time,Toast.LENGTH_SHORT).show();

        if(name.isEmpty() || description.isEmpty()){
            Toast.makeText(getApplicationContext(),"Please fill in all information",Toast.LENGTH_SHORT).show();
            saved=false;
        }
        else if(description.length()<25){
            Toast.makeText(getApplicationContext(),"Description must be atleast 25 characters long",Toast.LENGTH_SHORT).show();
            saved=false;
        }

        if(saved) {

            //get Firebase auth instance
            FirebaseAuth auth= FirebaseAuth.getInstance();
            //get user details
            FirebaseUser user = auth.getCurrentUser();
            //retrieve users facebook name
            String author = user.getDisplayName();

            // create an event
            eventDoc event = new eventDoc(id, name, author, description, category, location,date,time);


            dbm.createEvent(event);


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


    //function that retrieves selected item and displays it to users
    private void retrieveData(){

        //dbm.readEvent(id);
//        //get event class
//
//        //set event name and description
//        nameEdt.setText(event.getEventName());
//        descriptionEdt.setText(event.getEventDescription());
//
//        //set value of spinner
//        int pos = adapter.getPosition(event.getEventCategory());
//        categorySpin.setSelection(pos);

        //eventDoc event =dbm.readEvent(id);
        rootRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                //get event class
                 eventDoc event = snapshot.getValue(eventDoc.class);

                //set event name and description
                nameEdt.setText(event.getEventName());
                descriptionEdt.setText(event.getEventDescription());

                //set value of spinner
                int pos = adapter.getPosition(event.getEventCategory());
                categorySpin.setSelection(pos);


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void setLocation(View v){
        Intent i = new Intent(createEvent.this,locationMaps.class);
        i.putExtra("lat",locLat);
        i.putExtra("long",locLong);
        //to get location result
        startActivityForResult(i,10);
    }

    void getLocation(){
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
    }

    FragmentManager fragmentManager = getFragmentManager();

    selectDateFragment frag1 = new selectDateFragment();
    selectTimeFragment frag2 = new selectTimeFragment();

//

    public void showDateFragment(View v){

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        if(toggle) {
            toggle=false;
            dateToggle.setText("Select Date");

            timeToggle.setClickable(true);
            timeToggle.setBackgroundColor(getResources().getColor(R.color.colorAccent,null));

            dateEdt=(DatePicker)findViewById(R.id.datePickerFragment);
            date=dateEdt.getYear() + "-" + dateEdt.getMonth() + "-" + dateEdt.getDayOfMonth();
            fragmentTransaction.remove(frag1);
        }
        else{
            toggle=true;
            dateToggle.setText("Tap to Save");

            timeToggle.setClickable(false);
            timeToggle.setBackgroundColor(getResources().getColor(R.color.secondaryDarkColor,null));

            fragmentTransaction.add(R.id.layout_date_container, frag1);
            fragmentTransaction.show(frag1);
        }

        fragmentTransaction.commit();

    }

    public void showTimeFragment(View v){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        if(toggle) {
            toggle=false;
            timeToggle.setText("Select Time");

            dateToggle.setClickable(true);
            dateToggle.setBackgroundColor(getResources().getColor(R.color.colorAccent,null));

            timeEdt=(TimePicker)findViewById(R.id.timePickerFragment);
            time=timeEdt.getHour() + ":" + timeEdt.getMinute();
            fragmentTransaction.remove(frag2);
        }
        else{
            toggle=true;
            timeToggle.setText("Tap to Save");

            dateToggle.setClickable(false);
            dateToggle.setBackgroundColor(getResources().getColor(R.color.secondaryDarkColor,null));

            fragmentTransaction.add(R.id.layout_time_container, frag2);
            fragmentTransaction.show(frag2);
        }

        fragmentTransaction.commit();
    }

}
