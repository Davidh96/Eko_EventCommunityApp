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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;


public class LandingPage extends Activity {

    private List<eventDoc> eventList;
    private ListAdapter listAdapter;

    private ProgressBar loadingCircle;
    private SeekBar distanceBar;
    private TextView distanceText;

    private FirebaseUser user;
    private  FirebaseAuth auth;

    public String url;

    public static double locLat;
    public static double locLong;

    private float minDist = 1f;
    private float maxDist = 20.0f;
    private float step = 1;
    public float chosenDist =1.6f;

    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        user = FirebaseAuth.getInstance().getCurrentUser();

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.layoutRefreshLanding);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       swipeRefreshLayout.setRefreshing(false);
                       setListContents();
                    }
                },1000);
            }
        });

        //if the current user is the creator of this event, show event editing tools
        if(user!=null) {
            //create fragment for tools
            FragmentManager fragmentManager = getFragmentManager();

            CreateButtonFragment frag1 = new CreateButtonFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            //show fragment
            fragmentTransaction.add(R.id.edit_tools_layout_container, frag1);
            fragmentTransaction.show(frag1);
            fragmentTransaction.commit();
        }



        //views
        loadingCircle = (ProgressBar)findViewById(R.id.loadingCircle);
        distanceBar = (SeekBar)findViewById(R.id.seekbarDistanceLanding);
        distanceText = (TextView)findViewById(R.id.textDitanceLanding);

        distanceBar.setMax((int)((maxDist-minDist)/step));

        distanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                chosenDist = minDist + (i*step);
                distanceText.setText("" + chosenDist + "km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                LandingPage.this.url=getResources().getString(R.string.serverURL) + "/getEvents/" + locLat + "," + locLong + "/" + chosenDist;
                setListContents();

            }
        });


        eventList = new ArrayList<eventDoc>();

        getLocation();
        title = (TextView)findViewById(R.id.textTitleBarLanding);



    }

    @Override
    protected void onResume() {
        super.onResume();
        if (url != null) {
            setListContents();
        }
    }

    //retrieve event data from server and put it in the list
    void setListContents(){


        loadingCircle.setVisibility(View.VISIBLE);

        eventList= new ArrayList<eventDoc>();

        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the loading after completion
                        loadingCircle.setVisibility(View.INVISIBLE);

                        try {

                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);


                            String jsonOBj = obj.toString();


                            while(jsonOBj.length()>2) {

                                String startString = "{\"-";
                                int ind = jsonOBj.indexOf(startString) + startString.length();
                                String id = jsonOBj.substring(ind, jsonOBj.substring(ind).indexOf("\"") + ind);
;
                                JSONObject eventObj = obj.getJSONObject("-" + id);

                                //place event details into an eventDoc
                                eventDoc event = new eventDoc(eventObj.getString("id"), eventObj.getString("eventName"), eventObj.getString("eventAuthor"), eventObj.getString("eventAuthorID"),
                                        eventObj.getString("eventDescription"), eventObj.getString("eventCategory"), eventObj.getString("eventLocation"), eventObj.getString("eventDate"),
                                        eventObj.getString("eventTime"));

                                //add event to list
                                eventList.add(event);

                                String middle = "{\"-" + id + "\":" + eventObj.toString() + ",";
                                String last ="{\"-" + id + "\":" + eventObj.toString();

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
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);

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
                    Toast.makeText(getApplicationContext(), "You must sign in to view events", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    void getLocation(){

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            //when location is updated
            @Override
            public void onLocationChanged(Location location) {
                LandingPage.this.locLat=location.getLatitude();
                LandingPage.this.locLong=location.getLongitude();

                LandingPage.this.url=getResources().getString(R.string.serverURL) + "/getEvents/" + locLat + "," + locLong + "/" + chosenDist;

                setListContents();

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

        //request permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.INTERNET
                },10);
                return;
            }
        }


        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100000, 0, locationListener);
    }

    //is called when refresh button is clicked
    public void displayJoined(View v)
    {
        Intent displayJoinedEv = new Intent(LandingPage.this,ViewJoinedEvents.class);
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
        //TODO
        Intent createevent = new Intent(LandingPage.this,CreateEvent.class);
        startActivity(createevent);
    }

}
