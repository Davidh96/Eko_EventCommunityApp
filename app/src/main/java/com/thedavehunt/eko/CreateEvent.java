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
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class CreateEvent extends FragmentActivity implements SelectDateDialog.DateDialogListener, SelectTimeDialog.TimeDialogListener {

    private LocationManager locationManager;
    private LocationListener locationListener;

    private String url;

    private EditText nameEdit;
    private EditText descriptionEdit;
    private Spinner categorySpin;

    private Button timeToggle;
    private Button dateToggle;
    private Button locationButton;

    private TextView dateText;
    private TextView timeText;
    private TextView locationText;

    private FloatingActionButton CreateFab;

    private ShareDialog shareDialog;

    private String name = "";
    private String description = "";
    private String author="";
    private String authorID="";
    private String category = "";
    private String location="";
    private String date="";
    private String time="";
    private String id;

    private int requestCode=1;

    private int minTime=1000;
    private int minDistance =0;

    public static double locLong;
    public static double locLat;

    private ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        NetworkManager.getInstance(this);

        //set usrl to get event data
        url = getResources().getString(R.string.serverURLrecieve);

        Intent i = getIntent();
        //get id of event selected
        id = i.getStringExtra("id");

        //append id to url
        url += id;

        nameEdit = (EditText) findViewById(R.id.editNameCreate);
        descriptionEdit = (EditText) findViewById(R.id.editDescriptionCreate);
        categorySpin = (Spinner) findViewById(R.id.spinnerCategoryCreate);

        dateToggle = (Button)findViewById(R.id.buttonDateCreate);
        timeToggle = (Button)findViewById(R.id.buttonTimeCreate);
        locationButton = (Button)findViewById(R.id.buttonLocationCreate);

        dateText = (TextView)findViewById(R.id.textDateCreate);
        timeText=(TextView)findViewById(R.id.textTimeCreate);
        locationText = (TextView)findViewById(R.id.textLocationCreate);

        //FAB, used to allow creation of new events
        CreateFab = (FloatingActionButton) findViewById(R.id.fabCreate);

        //dialog for facebook sharing
        shareDialog = new ShareDialog(this);

        //ArrayAdapter using default spinner layout
        adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        categorySpin.setAdapter(adapter);

        //if an id was recieved, get date
        if(id!=null){
            retrieveData();
        }

        //on select of category spinner
        categorySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //check that item clicked it not invalid
                if(position!=0) {
                    category = parent.getItemAtPosition(position).toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //get user location
        getLocation();


    }

    //get result form chosen event location
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        //if location was set correctly
        if(resultCode == Activity.RESULT_OK){
            location = "" + data.getDoubleExtra("eLat",0) + "," + data.getDoubleExtra("eLong",0);
        }
        else{
            location = "" + locLat + "," + locLong;
        }

        locationText.setText("Location Saved");
    }

    //method for saving new events to the Firebase database
    protected void saveEvent(){
        boolean saved=true;
        int descriptionMin = 10;

        //get event name
        name=nameEdit.getText().toString();
        //get event description
        description=descriptionEdit.getText().toString();
        //used to check that charachter count
        String descNospc = description.replaceAll("\\s+","");

        //check to see if all data is present before saving
        if(name.isEmpty()){
            Toast.makeText(getApplicationContext(),"Please name your event",Toast.LENGTH_SHORT).show();
            saved=false;
        }

        else if(descNospc.length()<descriptionMin){
            Toast.makeText(getApplicationContext(),"Description must be atleast 25 characters long",Toast.LENGTH_SHORT).show();
            saved=false;
        }

        if(category.isEmpty()){
            Toast.makeText(getApplicationContext(),"Please select a category",Toast.LENGTH_SHORT).show();
            saved=false;
        }

        if(location.isEmpty()){
            Toast.makeText(getApplicationContext(),"Please give a location",Toast.LENGTH_SHORT).show();
            saved=false;
        }

        if(date.isEmpty()){
            Toast.makeText(getApplicationContext(),"Please give a date",Toast.LENGTH_SHORT).show();
            saved=false;
        }

        if(time.isEmpty()){
            Toast.makeText(getApplicationContext(),"Please give a time",Toast.LENGTH_SHORT).show();
            saved=false;
        }

        if(saved) {

            //get Firebase auth instance
            FirebaseAuth auth= FirebaseAuth.getInstance();
            //get user details
            FirebaseUser user = auth.getCurrentUser();
            //retrieve users facebook name
            author = user.getDisplayName();
            authorID = user.getUid();



            //create alert box to ask user if they wish to post to facebook
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            //alert title
            alert.setTitle(getResources().getString(R.string.facebookPostTitle))
                    //alert message
                    .setMessage(getResources().getString(R.string.facebookPostQuestion))
                    //if user clicks yes
                    .setPositiveButton(getResources().getString(R.string.positiveActionText), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //setup posting event to facebook
                            if (ShareDialog.canShow(ShareLinkContent.class)) {
                                //set post details
                                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                        .setQuote(name + ": " +description)
                                        .setContentUrl(Uri.parse(url))
                                        .build();

                                //display post information
                                shareDialog.show(linkContent);
                            }
                            createEventDoc();
                            finish();

                        }

                    })
                    //if user does not wish to post
                    .setNegativeButton(getResources().getString(R.string.negativeActionText), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            createEventDoc();
                            finish();
                        }
                    })

                    .show();

        }

    }

    private void createEventDoc(){

        // create an event
        EventDoc event = new EventDoc(id, name, author, authorID, description, category, location,date,time);

        NetworkManager.getInstance().postEvent(event,new NetworkManager.SomeCustomListener<JSONObject>()
        {
            @Override
            public void getResult(JSONObject result)
            {
                if (!result.toString().isEmpty())
                {
                    //display that event was created
                    Toast.makeText(getApplicationContext(),"Event Created",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //function that retrieves selected item and displays it to users
    private void retrieveData(){
        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            //getting the whole json object from the response
                            JSONObject eventObj = new JSONObject(response);



                            EventDoc event = new EventDoc(eventObj.getString("id"),eventObj.getString("eventName"),eventObj.getString("eventAuthor"),eventObj.getString("eventAuthorID"),
                                    eventObj.getString("eventDescription"),eventObj.getString("eventCategory"),eventObj.getString("eventLocation"),eventObj.getString("eventDate"),
                                    eventObj.getString("eventTime"));


                            //get event members
                            JSONArray members = eventObj.getJSONArray("members");

                            //add members to event
                            for(int i=0;i<members.length();i++){
                                JSONObject mem = members.getJSONObject(i);
                                EventMember member = new EventMember(mem.getString("id"));
                                event.addMembers(member);

                            }


                            //set event name and description
                            nameEdit.setText(event.getEventName());
                            descriptionEdit.setText(event.getEventDescription());

                            //set value of spinner
                            int pos = adapter.getPosition(event.getEventCategory());
                            categorySpin.setSelection(pos);

                            location= event.getEventLocation();
                            locationText.setText("Location Saved");

                            date=event.getEventDate();
                            dateText.setText(date);

                            time = event.getEventTime();
                            timeText.setText(time.substring(0,2) + ":" + time.substring(2,4));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

    //open up google maps
    public void setLocation(View v){
        Intent i = new Intent(CreateEvent.this,LocationMaps.class);
        i.putExtra("lat",locLat);
        i.putExtra("long",locLong);
        //to get location result
        startActivityForResult(i,requestCode);
    }

    void getLocation(){
        //get location services
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

        //check location services permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.INTERNET
                }, requestCode);
                return;
            }
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locationListener);
    }



    //shows date fragment
    public void showDateFragment(View v){

        SelectDateDialog dateDialog = new SelectDateDialog();
        dateDialog.show(getSupportFragmentManager(),"tag");
    }

    //shows time fragment
    public void showTimeFragment(View v){
        SelectTimeDialog timeDialog = new SelectTimeDialog();
        timeDialog.show(getSupportFragmentManager(),"tag");
    }

    public void saveEventClick(View v){
        saveEvent();
    }

    //gives date chosen by user
    @Override
    public void returnDate(String date) {
        this.date=date;
        dateText.setText(date);
    }

    //gives time chosen by user
    @Override
    public void returnTime(String time) {
        this.time=time;
        //must add :
        timeText.setText(time.substring(0,2) + ":" + time.substring(2,4));
    }
}
