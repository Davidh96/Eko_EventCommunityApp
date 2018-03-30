package com.thedavehunt.eko;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LandingPage extends Activity {

    private List<eventDoc> eventList;
    private ListAdapter listAdapter;

    private ProgressBar loadingCircle;
    private SeekBar distanceBar;
    private TextView distanceText;
    private Spinner categorySpinner;

    private FirebaseUser user;
    private  FirebaseAuth auth;

    public String url;

    public static double locLat;
    public static double locLong;

    public float distance=1;
    private float minDist = 1f;
    private float maxDist = 20.0f;
    private float step = 1;
    public float chosenDist =1.6f;

    public String category;

    private ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        //dbHelper.

        //get url for retrieving events
        url=getResources().getString(R.string.serverURLGetEvents);
        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();


        //views
        loadingCircle = (ProgressBar)findViewById(R.id.loadingCircle);
        distanceBar = (SeekBar)findViewById(R.id.seekbarDistanceLanding);
        distanceText = (TextView)findViewById(R.id.textDitanceLanding);
        categorySpinner = (Spinner)findViewById(R.id.spinnerCategoryLanding);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.layoutRefreshLanding);

        //default should be to show all categories
        category = "All";


        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    LandingPage.this.category = parent.getItemAtPosition(position).toString();
                }
                else{
                    LandingPage.this.category = "All";
                }

                setListContents(LandingPage.this.category);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //setup refresh listener
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       swipeRefreshLayout.setRefreshing(false);
                       //reload the list view
                       setListContents(category);
                    }
                },500);
            }
        });

        //if the current user is not a guest, show create event buuton
        if(user!=null) {
            //create fragment for tools
            FragmentManager fragmentManager = getFragmentManager();

            CreateButtonFragment createEvenetfrag = new CreateButtonFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            //show fragment
            fragmentTransaction.add(R.id.create_event_layout_container, createEvenetfrag);
            fragmentTransaction.show(createEvenetfrag);
            fragmentTransaction.commit();
        }

        //set the max distance user can choose
        distanceBar.setMax((int)((maxDist-minDist)/step));

        //when user changes distance
        distanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //get distance
                chosenDist = minDist + (i*step);
                distanceText.setText("" + chosenDist + "km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                LandingPage.this.distance = chosenDist;
                //once the swiping is finished reload list view with new distance
                setListContents(category);

            }
        });


        //get user location
        getLocation();


    }

    @Override
    protected void onResume() {
        super.onResume();
        setListContents(category);
    }

    //retrieve event data from server and put it in the list
    void setListContents(String filterCategory){

        loadingCircle.setVisibility(View.VISIBLE);

        //create event list to display in list view
        //needs to be cleared everytime
        eventList = new ArrayList<eventDoc>();

        getEvents(filterCategory);

        //initialise adapter
        listAdapter = new landingListAdapter(LandingPage.this,eventList);

        ListView list = (ListView)findViewById(R.id.listEventLanding);
        //set adapter for list view
        list.setAdapter(listAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //check if user or guest
                if(user!=null) {

                    Intent viewTask = new Intent(LandingPage.this, ViewEvent.class);

                    eventDoc evnt = (eventDoc) eventList.get(i);
                    Toast.makeText(getApplicationContext(), evnt.getId(), Toast.LENGTH_LONG);
                    viewTask.putExtra("id", evnt.getId());

                    startActivity(viewTask);
                }
                else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.guestViewEventToast), Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    void getEvents(final String filter){
        HashMap req = new HashMap();
        req.put("location",locLat + "," + locLong );
        req.put("distance", distance);
        req.put("userID", user.getUid());

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //Toast.makeText(getApplicationContext(),filterCategory,Toast.LENGTH_SHORT).show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,new JSONObject(req),
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //hiding the loading after completion
                    loadingCircle.setVisibility(View.INVISIBLE);

                    try {

                        //getting the whole json object from the response
                        JSONObject obj = response;


                        String jsonOBj = obj.toString();

                        //if more than one event
                        while(jsonOBj.length()>2) {

                            String startString = "{\"-";
                            int ind = jsonOBj.indexOf(startString) + startString.length();
                            String id = jsonOBj.substring(ind, jsonOBj.substring(ind).indexOf("\"") + ind);

                            JSONObject eventObj = obj.getJSONObject("-" + id);

                            //place event details into an eventDoc
                            eventDoc event = new eventDoc(eventObj.getString("id"), eventObj.getString("eventName"), eventObj.getString("eventAuthor"), eventObj.getString("eventAuthorID"),
                                    eventObj.getString("eventDescription"), eventObj.getString("eventCategory"), eventObj.getString("eventLocation"), eventObj.getString("eventDate"),
                                    eventObj.getString("eventTime"));

                            //filter out results based on category
                            if(filter.equals("All") || filter.equals(event.eventCategory)) {
                                //add event to list
                                eventList.add(event);
                            }


                            //get next event in list
                            String middle = "{\"-" + id + "\":" + eventObj.toString() + ",";
                            String last ="{\"-" + id + "\":" + eventObj.toString();

                            //if we haven't reached end of list
                            if(jsonOBj.indexOf(middle)>=0){
                                jsonOBj = jsonOBj.replace(middle, "{");
                            }
                            else{
                                jsonOBj = jsonOBj.replace(last,"{");
                            }


                        }

                        //initialise adapter
                        listAdapter = new landingListAdapter(LandingPage.this,eventList);

                        ListView list = (ListView)findViewById(R.id.listEventLanding);
                        //set adapter for list view
                        list.setAdapter(listAdapter);


                    } catch (JSONException e) {

                    }
                }},
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });

        requestQueue.add(jsonObjectRequest);
    }

    //get user location
    void getLocation(){

        //create location manager
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            //when location is updated
            @Override
            public void onLocationChanged(Location location) {
                //set location variables
                LandingPage.this.locLat=location.getLatitude();
                LandingPage.this.locLong=location.getLongitude();
                //will get user location when location listener is triggered
                setListContents(category);
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
                //open settings
                Intent _intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(_intent);
            }
        };

        //request permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //check permissions
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.INTERNET
                },10);
                return;
            }
        }

        //set when location is requested
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100000, 0, locationListener);
    }

    //is called when refresh button is clicked
    public void displayJoined(View v)
    {
        Intent displayJoinedEv = new Intent(LandingPage.this,ChatDisplay.class);
        startActivity(displayJoinedEv);
    }

    //is called when logout button is clicked
    public void logout(View v)
    {
        auth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        finish();
    }

    //called when FAB create button is clicked
    public void createEvent(View v)
    {
        Intent createevent = new Intent(LandingPage.this,CreateEvent.class);
        startActivity(createevent);
    }

}
